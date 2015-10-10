package com.xpensercpt.mkumar.xpensercpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkumar on 10/9/15.
 * Data source for Trip class
 */
public class TripDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context m_context;
    private String[] allColumns = { Trip.COLUMN_ID, Trip.COLUMN_NAME, Trip.COLUMN_DATE };

    public TripDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        m_context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTrip(String name, String date){
        ContentValues values = new ContentValues();
        values.put(Trip.COLUMN_NAME, name);
        values.put(Trip.COLUMN_DATE, date);
        long insertId = database.insert(Trip.TABLE_TRIPS, null, values);
        
        RcptHelper.createTripDirectory(m_context, (int)insertId);
        
        return insertId;
    }

    public void deleteTrip(Trip trip) {
        for (Receipt rcpt :
                trip.getReceipts()) {
            rcpt.deleteReceipt(m_context, database);
        }
        long id = trip.getPrimaryKey();
        RcptHelper.deleteTripFolder(m_context, (int)id);
        System.out.println("Trip deleted with id: " + id);
        database.delete(Trip.TABLE_TRIPS, Trip.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Trip> getAllTrips() {
        ArrayList<Trip> trips = new ArrayList<>();

        Cursor cursor = database.query(Trip.TABLE_TRIPS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trip trip = cursorToTrip(cursor);
            trips.add(trip);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return trips;
    }

    private Trip cursorToTrip(Cursor cursor) {
        Trip trip = new Trip();
        trip.setPrimaryKey(cursor.getLong(0));
        trip.setName(cursor.getString(1));
        trip.setDate(cursor.getString(2));
        return trip;
    }


}
