package com.xpensercpt.mkumar.xpensercpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mkq on 10/9/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "remitjoy.db";
    private static final int DATABASE_VERSION = 1;


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        insertReceipt(db, japanTripId,3114.0f, "JPY","Lunch",2,"08-19-2015","1");
        
    }

    private long insertTrip(SQLiteDatabase db, String name, String date){
        ContentValues values = new ContentValues();
        values.put(Trip.COLUMN_NAME, name);
        values.put(Trip.COLUMN_DATE, date);
        long insertId = db.insert(Trip.TABLE_TRIPS, null, values);
        return insertId;
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

        long insertId = db.insert(Receipt.TABLE_RECEIPTS, null, values);
        return insertId;
    }

    /*

insert into trips(name,date) values('Paris (Sample)','03-01-2015');
insert into trips(name,date) values('Japan (Sample)','08-18-2015');

insert into receipts(trip_id, amount, currency, type, type_order, date, photo) values (1,51.2, 'EUR','Dinner',3,'03-01-2015','1');
insert into receipts(trip_id, amount, currency, type, type_order, date, photo) values (1,95.0, 'EUR','Taxi',10,'03-01-2015','1');
insert into receipts(trip_id, amount, currency, type, type_order, date, photo) values (1,17.9, 'EUR','Lunch',2,'03-06-2015','1');
insert into receipts(trip_id, amount, currency, type, type_order, date, photo) values (2,7063.0, 'JPY','Dinner',3,'08-18-2015','1');
insert into receipts(trip_id, amount, currency, type, type_order, date, photo) values (2,3114.0, 'JPY','Lunch',2,'08-19-2015','1');

1|1|51.2|EUR|Dinner|3|03-01-2015|1|
2|1|95.0|EUR|Taxi|10|03-01-2015|1|
3|1|17.9|EUR|Lunch|2|03-06-2015|1|
4|2|7063.0|JPY|Dinner|3|08-18-2015|1|
5|2|3114.0|JPY|Lunch|2|08-19-2015|1|
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
