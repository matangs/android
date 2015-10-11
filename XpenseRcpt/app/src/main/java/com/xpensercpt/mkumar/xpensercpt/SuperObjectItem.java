package com.xpensercpt.mkumar.xpensercpt;

/**
 * Created by mkumar on 10/10/15.
 * This class will hold all the objects that will be shown in a list view
 */
public class SuperObjectItem {
    private Trip m_trip;
    private Receipt m_receipt;
    private ReceiptImage m_receiptImage;
    private int m_id;

    public SuperObjectItem(int id, Trip trip, Receipt rcpt, ReceiptImage rcptImg){
        m_trip = trip;
        m_receipt = rcpt;
        m_receiptImage = rcptImg;
        m_id = id;
    }

    public boolean isTrip(){
        return m_trip != null;
    }

    public boolean isReceipt(){
        return m_receipt != null;
    }

    public boolean isReceiptImage(){
        return m_receiptImage != null;
    }

    public Trip getTrip(){
        return m_trip;
    }
}

