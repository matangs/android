package com.xpensercpt.mkumar.xpensercpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by mkumar on 10/9/15.
 * Data source for Receipts
 */
public class ReceiptDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            Receipt.COLUMN_ID,
            Receipt.COLUMN_TRIP_ID,
            Receipt.COLUMN_AMOUNT,
            Receipt.COLUMN_CURRENCY,
            Receipt.COLUMN_TYPE,
            Receipt.COLUMN_TYPE_ORDER,
            Receipt.COLUMN_DATE,
            Receipt.COLUMN_PHOTO,
            Receipt.COLUMN_COMMENT
    };
    private Context m_context;

    public ReceiptDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        m_context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertReceipt(Receipt rcpt){
        return insertReceipt(rcpt.getTripKey(), rcpt.getAmount(), rcpt.getCurrency(), rcpt.getExpenseType(), (int)rcpt.getExpenseTypeOrder(), rcpt.getDate(), rcpt.getphoto(), rcpt.getComment());
    }

    public long insertReceipt(long trip_id, float amount, String currency, String type, int type_order, String date, String photo, String comment){
        ContentValues values = new ContentValues();
        values.put(Receipt.COLUMN_TRIP_ID, trip_id);
        values.put(Receipt.COLUMN_AMOUNT,amount);
        values.put(Receipt.COLUMN_CURRENCY,currency);
        values.put(Receipt.COLUMN_TYPE,type);
        values.put(Receipt.COLUMN_TYPE_ORDER,type_order);
        values.put(Receipt.COLUMN_DATE,date);
        values.put(Receipt.COLUMN_PHOTO,photo);
        if (comment != null && comment.length() > 0)
            values.put(Receipt.COLUMN_COMMENT, comment);
        else
            values.putNull(Receipt.COLUMN_COMMENT);


        return database.insert(Receipt.TABLE_RECEIPTS, null, values);
    }

    public void deleteReceipt(Receipt receipt) {
        receipt.deleteReceipt(m_context, database);
    }

    public ArrayList<Receipt> getAllReceipts(int tripId) {
        ArrayList<Receipt> rcpts = new ArrayList<>();
        Cursor cursor = database.query(Receipt.TABLE_RECEIPTS,
                allColumns, "trip_id = ?", new String[] {"" + tripId} , null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Receipt rcpt = cursorToReceipt(cursor);
            rcpts.add(rcpt);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return rcpts;
    }

    public Receipt getReceipt(int rcptId) {
        Cursor cursor = database.query(Receipt.TABLE_RECEIPTS,
                allColumns, "_id = ?", new String[] {"" + rcptId} , null, null, null);
        cursor.moveToFirst();
        if (cursor.isAfterLast())
            return null;
        return cursorToReceipt(cursor);
    }

    private Receipt cursorToReceipt(Cursor cursor) {
        Receipt rcpt = new Receipt();
        rcpt.setPrimaryKey(cursor.getLong(0));
        rcpt.setTripKey(cursor.getLong(1));
        rcpt.setAmount(cursor.getFloat(2));
        rcpt.setCurrency(cursor.getString(3));
        rcpt.setExpenseType(cursor.getString(4));
        rcpt.setExpenseTypeOrder(cursor.getLong(5));
        rcpt.setDate(cursor.getString(6));
        rcpt.setPhoto(cursor.getString(7));

        String comment = cursor.getString(8);
        if (comment != null && comment.length() > 0)
            rcpt.setComment(comment);
        else
            rcpt.setComment(null);

        return rcpt;
    }

    public long updateReceipt(Receipt rcpt){
        ContentValues values = new ContentValues();
        values.put(Receipt.COLUMN_AMOUNT,rcpt.getAmount());
        values.put(Receipt.COLUMN_CURRENCY,rcpt.getCurrency());
        values.put(Receipt.COLUMN_TYPE,rcpt.getExpenseType());
        values.put(Receipt.COLUMN_TYPE_ORDER,rcpt.getExpenseTypeOrder());
        values.put(Receipt.COLUMN_DATE,rcpt.getDate());
        values.put(Receipt.COLUMN_PHOTO,rcpt.getphoto());
        if (rcpt.getComment() != null && rcpt.getComment().length() > 0)
            values.put(Receipt.COLUMN_COMMENT, rcpt.getComment());
        else
            values.putNull(Receipt.COLUMN_COMMENT);

        return database.update(Receipt.TABLE_RECEIPTS, values, "_id = ?", new String[]{"" + rcpt.getPrimaryKey()});

    }

}
