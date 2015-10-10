package com.xpensercpt.mkumar.xpensercpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import java.io.File;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Log;

/**
 * Created by mkumar on 10/9/15.
 * SqlHelper to create database when the app is loaded first time.
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "remitjoy.db";
    private static final int DATABASE_VERSION = 1;
    private Context m_context;


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database creation sql statement
        String TRIPS_CREATE = "create table " + Trip.TABLE_TRIPS +
                "( " +
                    Trip.COLUMN_ID + " integer primary key autoincrement, " +
                    Trip.COLUMN_NAME + " text not null, " +
                    Trip.COLUMN_DATE + " text not null " +
                ");";

        String RECEIPTS_CREATE = "create table " + Receipt.TABLE_RECEIPTS +
                "( " +
                    Receipt.COLUMN_ID + " integer primary key autoincrement, " +
                    Receipt.COLUMN_TRIP_ID + " integer, " +
                    Receipt.COLUMN_AMOUNT + " real, " +
                    Receipt.COLUMN_CURRENCY + " text not null, " +
                    Receipt.COLUMN_TYPE + " text not null, " +
                    Receipt.COLUMN_TYPE_ORDER + " integer, " +
                    Receipt.COLUMN_DATE + " text not null, " +
                    Receipt.COLUMN_PHOTO + " text not null, " +
                    Receipt.COLUMN_COMMENT + " text, " +
                    "FOREIGN KEY(" + Receipt.COLUMN_TRIP_ID + ") REFERENCES " +  Trip.TABLE_TRIPS +  "(" + Receipt.COLUMN_ID + ")" +
                ");";

        db.execSQL(TRIPS_CREATE);
        db.execSQL(RECEIPTS_CREATE);
        
        long parisTripId = insertTrip(db,"Paris (Sample)","03-01-2015");
        long japanTripId = insertTrip(db, "Japan (Sample)","08-18-2015");
        
        insertReceipt(db, parisTripId,51.2f, "EUR","Dinner",3,"03-01-2015","1");
        insertReceipt(db, parisTripId,95.0f, "EUR","Taxi",10,"03-01-2015","1");
        insertReceipt(db, parisTripId,17.9f, "EUR","Lunch",2,"03-06-2015","1");

        insertReceipt(db, japanTripId,7063.0f, "JPY","Dinner",3,"08-18-2015","1");
        insertReceipt(db, japanTripId,8391.0f, "JPY","Lunch",2,"08-19-2015","1");

        // create trip directory
        RcptHelper.createTripDirectory(m_context, (int)parisTripId);
        RcptHelper.createTripDirectory(m_context, (int)japanTripId);

        // now move 5 images to their right place
        copyImgToApp("" + parisTripId, "1.1", R.drawable.r1_1);
        copyImgToApp("" + parisTripId, "2.1", R.drawable.r2_1);
        copyImgToApp("" + parisTripId, "3.1", R.drawable.r3_1);
        copyImgToApp("" + japanTripId, "4.1", R.drawable.r4_1);
        copyImgToApp("" + japanTripId, "5.1", R.drawable.r5_1);
    }

    private void copyImgToApp(String tripId, String rcptName, int resourceId){
        File file = new File(m_context.getExternalFilesDir(null) + "/" + tripId, rcptName);
        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = m_context.getResources().openRawResource(resourceId);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            int test = is.read(data);
            assert test > 0;
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }



    private long insertTrip(SQLiteDatabase db, String name, String date){
        ContentValues values = new ContentValues();
        values.put(Trip.COLUMN_NAME, name);
        values.put(Trip.COLUMN_DATE, date);
        return db.insert(Trip.TABLE_TRIPS, null, values);
    }
    
    private long insertReceipt(SQLiteDatabase db, long trip_id, float amount, String currency, String type, int type_order, String date, String photo){
        ContentValues values = new ContentValues();
        values.put(Receipt.COLUMN_TRIP_ID, trip_id);
        values.put(Receipt.COLUMN_AMOUNT,amount);
        values.put(Receipt.COLUMN_CURRENCY,currency);
        values.put(Receipt.COLUMN_TYPE,type);
        values.put(Receipt.COLUMN_TYPE_ORDER,type_order);
        values.put(Receipt.COLUMN_DATE,date);
        values.put(Receipt.COLUMN_PHOTO,photo);

        return db.insert(Receipt.TABLE_RECEIPTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
