package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.xpensercpt.mkumar.xpensercpt.swipe.SwipeToDismissTouchListener;
import com.xpensercpt.mkumar.xpensercpt.swipe.adapter.ListViewAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TripsActivity extends AppCompatActivity {

    private Trip m_trip;
    private String m_tripName;
    private ReceiptDataSource m_rcptDataSource;
    private ReceiptAdapter m_rcptAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TripsActivity.this.printPDF(TripsActivity.this.m_trip.getPrimaryKey());
            }
        });

        setStatusBarColor();
        addedRcptListView();
        onAddRcptBtnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int prevCount = m_trip.getReceipts().size();

        m_rcptDataSource.open();
        ArrayList<Receipt> newRcpts = m_rcptDataSource.getAllReceipts((int) m_trip.getPrimaryKey());
        m_rcptDataSource.close();

        int newCount = newRcpts.size();
        if (newCount > prevCount) {
            m_trip.getReceipts().clear();
            m_trip.getReceipts().addAll(newRcpts);
            m_rcptAdapter.notifyDataSetChanged();
        }
    }

    private void addedRcptListView(){
        m_rcptDataSource = new ReceiptDataSource(this);
        Intent intent = getIntent();
        long tripId = intent.getLongExtra("TripID",-1);
        m_trip = new Trip(tripId);
        m_tripName = intent.getStringExtra("TripName");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(m_tripName);

        m_rcptAdapter = new ReceiptAdapter(this,R.layout.trip_view_row_item,m_trip.getReceipts());

        ListView listView = (ListView)findViewById(R.id.added_rcpt_list);
        listView.setAdapter(m_rcptAdapter);


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
                                m_rcptAdapter.remove(m_trip.getReceipts().get(position));
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
                Receipt rcpt = m_trip.getReceipts().get(position);
                // launch intent tripsactivity
                Intent myIntent = new Intent(TripsActivity.this, ReceiptActivity.class);
                myIntent.putExtra("ReceiptID", rcpt.getPrimaryKey()); //Optional parameters
                myIntent.putExtra("TripID",rcpt.getTripKey());
                TripsActivity.this.startActivity(myIntent);
            }
        });
    }

    private void onAddRcptBtnClick(){
        Button addButton = (Button) findViewById(R.id.addNewRcptButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(TripsActivity.this, ReceiptActivity.class);
                myIntent.putExtra("ReceiptID", -1);
                myIntent.putExtra("TripID", m_trip.getPrimaryKey());
                TripsActivity.this.startActivity(myIntent);
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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void printPDF(long tripid) {

        m_rcptDataSource.open();
        ArrayList<Receipt> rcpts = m_rcptDataSource.getAllReceipts((int)tripid);
        m_rcptDataSource.close();
        if (rcpts.isEmpty())
            return;

        String msg = emailMsg(rcpts);

        // Create a shiny new (but blank) PDF document in memory
        // We want it to optionally be printable, so add PrintAttributes
        // and use a PrintedPdfDocument. Simpler: new PdfDocument().
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME).//.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 100, 100)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();
        PdfDocument document = new PrintedPdfDocument(this, printAttrs);

        int pageIndex = 1;
        for (Receipt rcpt :
                rcpts) {
            ReceiptImage rcptImg = new ReceiptImage();
            rcptImg.load(rcpt,this);
            ArrayList<ReceiptImage.ReceiptImageData> dataArr = rcptImg.getImageDataArr();

            String message;
            if (rcpt.getComment() != null && rcpt.getComment().length() > 0) {
                message = rcpt.getDate() + " - " + rcpt.getExpenseType()  + " - " + rcpt.getCurrency() + " - " + rcpt.getAmount() + ", Comments - " + rcpt.getComment();
            }
            else {
                message = rcpt.getDate() + " - " + rcpt.getExpenseType()  + " - " + rcpt.getCurrency() + " - " + rcpt.getAmount();
            }

            for (ReceiptImage.ReceiptImageData data :
                    dataArr) {
                // create a new page from the PageInfo
                // crate a page description
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(612, 792, pageIndex).create();
                pageIndex++;

                PdfDocument.Page page = document.startPage(pageInfo);
                Bitmap myBitmap = data.getImage();

                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();
                Canvas canvas = page.getCanvas();
                Bitmap newbmp = Bitmap.createScaledBitmap(myBitmap, (int) (width * 0.45), (int) (height * 0.45), false);

                ColorMatrix ma = new ColorMatrix();
                ma.setSaturation(0);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(ma));


                if (width > height)
                {
                    int newHt = (int)(300.0*height/width);
                    canvas.drawBitmap(newbmp, null, new Rect(100, 100, 400, 100+newHt), paint);
                    canvas.drawText(message, 100, newHt + 50, paint);
                }
                else if (width < height)
                {
                    int newwidth = (int)(300.0*width/height);
                    canvas.drawBitmap(newbmp, null, new Rect(100, 100, 100+newwidth, 400), paint);
                    canvas.drawText(message, 100, 450, paint);
                }
                else
                {
                    canvas.drawBitmap(newbmp, null, new Rect(100, 100, 400, 400), paint);
                    canvas.drawText(message, 100, 450, paint);
                }
                document.finishPage(page);

            }
        }


        try {
            File pdfDirPath = new File(getFilesDir(), "pdfs");
            if (pdfDirPath.exists() || pdfDirPath.mkdirs()) {
                File file = new File(pdfDirPath, "pdfsend.pdf");
                String absPath = file.getAbsolutePath();
                Uri contentUri = Uri.fromFile(file);
                try {
                    contentUri = FileProvider.getUriForFile(
                            this,
                            "com.xpensercpt.fileprovider",
                            file);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: ");
                }

                OutputStream os = new FileOutputStream(file);
                document.writeTo(os);
                document.close();
                os.close();
                shareDocument(contentUri, msg);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }

    private void viewPdf(String absPath){
        Intent myIntent = new Intent(this, PDFViewActivity.class);
        myIntent.putExtra("ABS_PATH", absPath);
        startActivity(myIntent);
    }

    private String emailMsg(ArrayList<Receipt> rcpts){

        StringBuilder builder = new StringBuilder();
        builder.append("Hello there,<br /><br />The PDF containing all reciept images are attached for your filing purposes. Hope you enjoyed the ease of use of XpenseRcpt. Please find your expenses in a tabular format below.<br /><br />");
        builder.append("Amount - Currency - Date - Expense Type  (comment)<br />");
        for (Receipt rcpt :
                rcpts) {
            String comment = "";
            if (rcpt.getComment() != null)
                comment = " (" + rcpt.getComment() + ")";
            String str = rcpt.getAmount() + " - "+ rcpt.getCurrency() + " - " + rcpt.getDate() + " - " + rcpt.getExpenseType() + "  " + comment + "<br />";
            builder.append(str);
        }
        builder.append("<br/>Sent using XpenseRcpt for Android.</p>");

        return builder.toString();

    }


    private void shareDocument(Uri uri, String msg) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/xml");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, m_tripName + " - Trip summary");

        //intent.putExtra(Intent.EXTRA_TEXT, msg);
        intent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(msg));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }


}
