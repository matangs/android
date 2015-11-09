package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xpensercpt.mkumar.xpensercpt.swipe.SwipeToDismissTouchListener;
import com.xpensercpt.mkumar.xpensercpt.swipe.adapter.ListViewAdapter;

import junit.framework.Assert;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ReceiptActivity extends AppCompatActivity{

    private ReceiptDataSource m_rcptDataSource;
    private Receipt m_rcpt;
    private Receipt m_origRcpt;
    private long m_tripId;
    private ReceiptImage m_receiptImageHelper;
    private ArrayList<String> m_deletedImageArr;
    private ArrayList<String> m_currencyShortName;
    private ArrayList<String> m_expenseType;
    private int[] m_typeOrder;
    private boolean m_isUpdating;
    private ReceiptImageAdapter m_imageAdapter;

    public static class MyDatePickerFragment extends AppCompatDialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            ReceiptActivity activity = (ReceiptActivity) getActivity();
            EditText dateText = (EditText) activity.findViewById(R.id.editTextRcptDate);
            dateText.setText(String.format("%02d", month+1) +  "-" + String.format("%02d", day) + "-" + year);
        }
    }
    public void showDatePickerDialog(View v) {

        AppCompatDialogFragment newFragment = new MyDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
        //newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        m_deletedImageArr = new ArrayList<>();
        m_receiptImageHelper = new ReceiptImage();

        Resources res = getResources();
        m_currencyShortName = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.currency_short_name_arr)));
        m_expenseType = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.expense_type_array)));
        m_typeOrder = res.getIntArray(R.array.expense_type_array_order);

        /*SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("var1", myvar);
        editor.commit();*/


        setReceipt();
        setStatusBarColor();
        //setSaveButton();
        setupSaveButton();
        setDateEditBox();
        setupDefData();
        addImageListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int prevCount = m_receiptImageHelper.getImageDataArr().size();

        setReceipt();

        int newCount = m_receiptImageHelper.getImageDataArr().size();
        if (newCount > prevCount) {
            m_imageAdapter.notifyDataSetChanged();
        }
    }

    private void addImageListView(){
        m_imageAdapter = new ReceiptImageAdapter(this,R.layout.receipt_image_row_item,m_receiptImageHelper.getImageDataArr());
        ListView listView = (ListView)findViewById(R.id.rcpt_images_list);
        listView.setAdapter(m_imageAdapter);


        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(listView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                m_imageAdapter.remove(m_receiptImageHelper.getImageDataArr().get(position));
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                    return;
                }


                String absPath = ReceiptActivity.this.m_receiptImageHelper.getImageDataArr().get(position).getAbsPath();
                Intent myIntent = new Intent(ReceiptActivity.this, ReceiptImageActivity.class);
                myIntent.putExtra("ABS_PATH",absPath);
                startActivity(myIntent);
            }
        });
    }

    private void setupDefData(){
        int currencyIndex;
        int expenseTypeIndex;
        String rcptDate;
        float amount = 0;
        String comment = null;

        if (m_rcpt == null){
            SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            currencyIndex = preferences.getInt("ReceiptCurrency",0);
            expenseTypeIndex = preferences.getInt("ExpenseType",0);
            rcptDate = preferences.getString("DefaultReceiptDate", "");
        } else{

            expenseTypeIndex = m_expenseType.indexOf(m_rcpt.getExpenseType());
            currencyIndex = m_currencyShortName.indexOf(m_rcpt.getCurrency());
            rcptDate = m_rcpt.getDate();
            amount = m_rcpt.getAmount();
            comment = m_rcpt.getComment();

        }

        TextView amountTextView = (TextView)this.findViewById(R.id.editTextRcptAmount);
        amountTextView.setText("" + amount);

        EditText dateText = (EditText) this.findViewById(R.id.editTextRcptDate);
        dateText.setText(rcptDate);

        Spinner expenseTypeSpinner = (Spinner) this.findViewById(R.id.spinnerExpenseType);
        expenseTypeSpinner.setSelection(expenseTypeIndex);

        Spinner currencyTypeSpinner = (Spinner) this.findViewById(R.id.spinnerCurrencyName);
        currencyTypeSpinner.setSelection(currencyIndex);

        if (comment != null && !comment.isEmpty()) {
            EditText commentEditText = (EditText) this.findViewById(R.id.editTextRcptNote);
            commentEditText.setText(comment);
        }
    }

    private void setupSaveButton(){
        Button saveButton = (Button) findViewById(R.id.saveRcptButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView amountTextView = (TextView) ReceiptActivity.this.findViewById(R.id.editTextRcptAmount);
                String amount = amountTextView.getText().toString();

                if (amount.isEmpty() || m_receiptImageHelper.getImageDataArr().size() < 1) {
                    new AlertDialog.Builder(ReceiptActivity.this)
                            .setTitle("Data missing")
                            .setMessage("You must select amount and a photo to save the receipt")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }


                saveReceipt();
                //[[self navigationController] popViewControllerAnimated:YES];

            }
        });
    }

    private void saveReceipt(){
        updateReceipt();
        if (m_isUpdating)
            m_rcptDataSource.updateReceipt(m_rcpt);
        else
            m_rcptDataSource.insertReceipt(m_rcpt);

        saveImagesToAppFolder();

        finish();
    }

    private void saveImagesToAppFolder(){

        for (String imgId :
                m_deletedImageArr) {
            File imgFile = m_rcpt.imageFile(imgId, this.getApplicationContext());
            if (imgFile.exists()) {
                boolean deleted = imgFile.delete();
                Assert.assertEquals(deleted, true);
            }
        }

        for (ReceiptImage.ReceiptImageData data :
                m_receiptImageHelper.getImageDataArr()) {
            if (!data.isNew())
                continue;
            File imgFile = m_rcpt.imageFile(data.getId() + "", this.getApplicationContext());
            if (imgFile.exists()) {
                boolean deleted = imgFile.delete();
                Assert.assertEquals(deleted, true);
            }

            // TODO: save image file on the disk at this point.
            //NSData *imageData = UIImageJPEGRepresentation(data.m_image, 0.3);
            //[imageData writeToFile:destinationPath atomically:true];


        }
    }

    private void updateReceipt(){
        if (m_rcpt == null){
            m_rcpt = new Receipt();
        }
        m_rcpt.setTripKey(m_tripId);

        TextView amountTextView = (TextView)this.findViewById(R.id.editTextRcptAmount);
        float amount = Float.parseFloat(amountTextView.getText().toString());
        m_rcpt.setAmount(amount);


        EditText dateText = (EditText) this.findViewById(R.id.editTextRcptDate);
        String date = dateText.getText().toString();
        m_rcpt.setDate(date);

        Spinner expenseTypeSpinner = (Spinner) this.findViewById(R.id.spinnerExpenseType);
        String expenseType = (String)expenseTypeSpinner.getSelectedItem();
        int expenseTypeOrder = m_typeOrder[expenseTypeSpinner.getSelectedItemPosition()];
        m_rcpt.setExpenseType(expenseType);
        m_rcpt.setExpenseTypeOrder(expenseTypeOrder);

        Spinner currencyTypeSpinner = (Spinner) this.findViewById(R.id.spinnerCurrencyName);
        int selIndex = currencyTypeSpinner.getSelectedItemPosition();
        m_rcpt.setCurrency(m_currencyShortName.get(selIndex));

        EditText commentEditText = (EditText) this.findViewById(R.id.editTextRcptNote);
        String comment = commentEditText.getText().toString();
        if (comment.isEmpty())
            m_rcpt.setComment("");
        else
            m_rcpt.setComment(comment);

        m_rcpt.setPhoto(m_receiptImageHelper.getPhotoStr());
    }

    private void setReceipt(){
        m_rcptDataSource = new ReceiptDataSource(this);
        Intent intent = getIntent();
        long rcptId = intent.getLongExtra("ReceiptID",-1);
        m_tripId = intent.getLongExtra("TripID",-1);
        m_origRcpt = null;
        if (rcptId != -1){
            m_rcptDataSource.open();
            m_rcpt = m_rcptDataSource.getReceipt((int)rcptId);
            m_rcptDataSource.close();

            m_origRcpt = new Receipt();
            m_origRcpt.transferData(m_rcpt);
            m_isUpdating = true;
            m_receiptImageHelper.load(m_rcpt.getphoto(), m_rcpt, this.getApplicationContext());
        }
        else
            m_isUpdating = false;

    }

    public void onCameraClick(View view){
        //printPDF();
        dispatchTakePictureIntent();
        //dispatchSelectPictureIntent();
        //return;

        //Intent myIntent = new Intent(this, ReceiptImageActivity.class);
        //startActivity(myIntent);

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            // TODO: after taking a picture - save it in the temp directory and use it's bmp in the ReceiptActivity
            String absPath = ReceiptActivity.this.m_receiptImageHelper.getImageDataArr().get(0).getAbsPath();
            Bitmap imageBitmap = BitmapFactory.decodeFile(absPath);
            m_receiptImageHelper.addNewImage(imageBitmap, absPath);
            m_imageAdapter.notifyDataSetChanged();

            /*
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent myIntent = new Intent(this, ReceiptImageActivity.class);
            myIntent.putExtra("data",imageBitmap);
            startActivity(myIntent);
            */
        }
    }


/*
    protected static final int REQUEST_PICK_IMAGE = 1;
    protected static final int REQUEST_PICK_CROP_IMAGE = 2;
    private static final int SELECT_PHOTO = 100;
    private void dispatchSelectPictureIntent(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        Intent myIntent = new Intent(this, ReceiptImageActivity.class);
                        myIntent.putExtra("data",yourSelectedImage);
                        startActivity(myIntent);
                    }
                    catch (FileNotFoundException ex){

                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {

            case SELECT_PHOTO:
                if (RESULT_OK == resultCode) {
                    Uri imageUri = intent.getData();
                    Bitmap bitmap;
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        Intent myIntent = new Intent(this, ReceiptImageActivity.class);
                        myIntent.putExtra("data", bitmap);
                        startActivity(myIntent);

                        //mImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case REQUEST_PICK_CROP_IMAGE:
                Bitmap selectedImage = BitmapFactory.decodeFile(Environment
                        .getExternalStorageDirectory() + "/temp.jpg");
                Intent myIntent = new Intent(this, ReceiptImageActivity.class);
                myIntent.putExtra("data",selectedImage);
                startActivity(myIntent);

                //mImageView.setImageBitmap(selectedImage);
                break;
        }
    }
*/
    private void setDateEditBox(){
        EditText myEditText = (EditText) findViewById(R.id.editTextRcptDate);

        myEditText.setInputType(InputType.TYPE_NULL);
        myEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        myEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });
    }

    private void setSaveButton(){
        Button saveButton = (Button) findViewById(R.id.saveRcptButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if (name.isEmpty()) {

                    new AlertDialog.Builder(AddTripActivity.this)
                            .setTitle("Nothing to add")
                            .setMessage("Did you forget to enter a name for the trip?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;

                }
                */

                /*
                TripDataSource dm = new TripDataSource(AddTripActivity.this);
                dm.open();
                dm.insertTrip(name, date);
                dm.close();
                */
                ReceiptActivity.this.finish();

            }
        });
    }


    @TargetApi(21)
    void setStatusBarColor(){
        if (Build.VERSION.SDK_INT < 21)
            return;

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkest_orange));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent mShareIntent;
    private OutputStream os;
    public void printPDF() {

        // Create a shiny new (but blank) PDF document in memory
        // We want it to optionally be printable, so add PrintAttributes
        // and use a PrintedPdfDocument. Simpler: new PdfDocument().
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).//.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 300, 300)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();
        PdfDocument document = new PrintedPdfDocument(this, printAttrs);

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 300, 1).create();

        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);

        // repaint the user's text into the page
        //View content = findViewById(R.id.textArea);
        //content.draw(page.getCanvas());

        File imgFile = new File(this.getApplicationContext().getDir("1",0), "2_1.jpg");
        if(imgFile.exists()){
            String str = imgFile.getAbsolutePath();

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(myBitmap,null,new Rect(10,10,300,300), null);
        }


        // do final processing of the page
        document.finishPage(page);

        // Here you could add more pages in a longer doc app, but you'd have
        // to handle page-breaking yourself in e.g., write your own word processor...

        // Now write the PDF document to a file; it actually needs to be a file
        // since the Share mechanism can't accept a byte[]. though it can
        // accept a String/CharSequence. Meh.
        try {
            File pdfDirPath = new File(getFilesDir(), "pdfs");
            if (pdfDirPath.mkdirs()) {
                File file = new File(pdfDirPath, "pdfsend.pdf");
                Uri contentUri = FileProvider.getUriForFile(this, "com.xpensercpt.fileprovider", file);
                os = new FileOutputStream(file);
                document.writeTo(os);
                document.close();
                os.close();

                shareDocument(contentUri);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }

    private void shareDocument(Uri uri) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("application/pdf");
        // Assuming it may go via eMail:
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend");
        // Attach the PDf as a Uri, since Android can't take it as bytes yet.
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(mShareIntent);

    }

}
