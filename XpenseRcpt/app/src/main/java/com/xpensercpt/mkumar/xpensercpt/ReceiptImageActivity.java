package com.xpensercpt.mkumar.xpensercpt;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiptImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        setStatusBarColor();

        //test();
        setRotate();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Bitmap imageBitmap = null;

        if (getIntent().hasExtra("ABS_PATH")){
            String path = getIntent().getStringExtra("ABS_PATH");
            imageBitmap = BitmapFactory.decodeFile(path);
        }

        if (imageBitmap == null && getIntent().hasExtra("data"))
            imageBitmap = (Bitmap) extras.get("data");
        else if (getIntent().hasExtra("Image"))
            imageBitmap = (Bitmap) extras.get("Image");

        if (imageBitmap == null){
            if(getIntent().hasExtra("byteArray")) {
                imageBitmap = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            }
        }

        if (imageBitmap != null){
            ImageView rcptImage = (ImageView)findViewById(R.id.imageViewRcptDetail);
            rcptImage.setImageBitmap(imageBitmap);
        }
        else{

            File imgFile = new File(this.getApplicationContext().getDir("1",0), "2_1.jpg");
            if(imgFile.exists()){
                String str = imgFile.getAbsolutePath();

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                ImageView rcptImage = (ImageView)findViewById(R.id.imageViewRcptDetail);
                rcptImage.setImageBitmap(myBitmap);

            }
        }


    }

    private void setRotate(){
        Button rotateButton = (Button) findViewById(R.id.rotateImgButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView rcptImage = (ImageView)findViewById(R.id.imageViewRcptDetail);
                rcptImage.setImageBitmap(RotateBitmap(((BitmapDrawable)rcptImage.getDrawable()).getBitmap(),90));

                /*float angle = rcptImage.getRotation();
                if (angle < 270)
                    angle = angle + 90;
                else
                    angle = 0;
                rcptImage.setRotation(angle);
                */
                }
        });
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        //Bitmap source = BitmapFactory.decodeResource(this.getResources(), R.drawable.your_img);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

}
