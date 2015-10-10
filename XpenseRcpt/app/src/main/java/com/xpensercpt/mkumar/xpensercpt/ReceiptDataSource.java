package com.xpensercpt.mkumar.xpensercpt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by mkq on 10/9/15.
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

    public ReceiptDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
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
        if (comment != null)
            values.put(Receipt.COLUMN_COMMENT, comment);

        long insertId = database.insert(Receipt.TABLE_RECEIPTS, null, values);
        return insertId;
    }

    public void deleteReceipt(Receipt receipt) {

        /*

        delete all photos

        NSArray* arr = [rcpt.m_photo componentsSeparatedByString:@","];

        for (NSString* indexStr in arr)
        {
            NSString* photoPath = [rcpt imagePath:indexStr];
            if ([[NSFileManager defaultManager] fileExistsAtPath:photoPath])
            {
                NSError* error;
                [[NSFileManager defaultManager] removeItemAtPath: photoPath error: &error];
            }
        }

        */

        long id = receipt.getPrimaryKey();
        System.out.println("Receipt deleted with id: " + id);
        database.delete(Receipt.TABLE_RECEIPTS, Receipt.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Receipt> getAllReceipts(int tripId) {
        ArrayList<Receipt> rcpts = new ArrayList<Receipt>();
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

        long updateId = database.update(Receipt.TABLE_RECEIPTS, values, "_id = ?", new String[]{"" + rcpt.getPrimaryKey()});
        return updateId;

    }

}
