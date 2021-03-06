package com.xpensercpt.mkumar.xpensercpt;

import java.util.ArrayList;
//import com.xpensercpt.mkumar.xpensercpt.Receipt;

/**
 * Created by mkumar on 10/9/15.
 * Trip class to store data related to a trip and optionally all it's receipts
 */
public class Trip {

    public static final String TABLE_TRIPS = "trips";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";

    private long m_primaryKey = -1; //id
    private String m_name; //name
    private String m_date; //date

    private ArrayList<Receipt> m_receipts;

    public Trip(long tripId){
        m_primaryKey = tripId;
        m_receipts = new ArrayList<>();
    }

    public void setPrimaryKey(long id){
        m_primaryKey = id;
    }

    public long getPrimaryKey(){
        return m_primaryKey;
    }

    public void setName(String nameIn){
        m_name = nameIn;
    }

    public String getName(){
        return m_name;
    }

    public void setDate(String dateIn){
        m_date = dateIn;
    }

    public String getDate(){
        return m_date;
    }

    public ArrayList<Receipt> getReceipts(){ return m_receipts; }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return m_name;
    }

/*

-(NSString*)tripDirectoryPath{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* documentsDirectory = [paths objectAtIndex:0];
    return [documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%lu", (long)self.m_primaryKey]];
}

     */
}
