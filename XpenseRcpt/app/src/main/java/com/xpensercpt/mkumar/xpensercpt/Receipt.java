package com.xpensercpt.mkumar.xpensercpt;

/**
 * Created by mkq on 10/9/15.
 */
public class Receipt {

    public static final String TABLE_RECEIPTS = "receipts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TYPE_ORDER = "type_order";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_COMMENT = "comment";

    private long m_primaryKey; //id
    private long m_tripKey; //trip_id

    private float  m_amount; //amount
    private String m_currency; //currency

    private String m_expenseType; // type
    private long m_expenseTypeOrder; // type_order

    private String m_date; // date

    private String m_photo; // photo1

    private String m_comment; // comment

    public void setPrimaryKey(long id){
        m_primaryKey = id;
    }

    public long getPrimaryKey(){
        return m_primaryKey;
    }

    public void  setTripKey(long keyIn){
        m_tripKey = keyIn;
    }

    public long getTripKey(){
        return m_tripKey;
    }

    public void setAmount(float val){
        m_amount = val;
    }

    public float getAmount(){
        return m_amount;
    }

    public void setCurrency(String val){
        m_currency = val;
    }

    public String getCurrency(){
        return m_currency;
    }

    public void setExpenseType(String val){
        m_expenseType = val;
    }

    public String getExpenseType(){
        return m_expenseType;
    }

    public void setExpenseTypeOrder(long val){
        m_expenseTypeOrder = val;
    }

    public long getExpenseTypeOrder(){
        return m_expenseTypeOrder;
    }

    public void setDate(String val){
        m_date = val;
    }

    public String getDate(){
        return m_date;
    }

    public void setPhoto(String val){
        m_photo = val;
    }

    public String getphoto(){
        return m_photo;
    }

    public void setComment(String val){
        m_comment = val;
    }

    public String getComment(){
        return m_comment;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return m_expenseType;
    }


//-(void)saveReceipt;
//-(void)updateReceipt;
//+(NSMutableArray*)loadReceipts:(NSInteger)tripId;
//+(void)deleteReceipt:(Receipt*)rcpt;

    String imagePath(String imgId){
        return null;
        /*
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString* documentsDirectory = [paths objectAtIndex:0];
        return [documentsDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%lu/%lu.%@.jpg", (long)self.m_tripKey, (long)self.m_primaryKey,imgId]];

        */
    }

    boolean isSame(Receipt rcpt){

        if (rcpt.m_amount != m_amount || rcpt.m_primaryKey != rcpt.m_primaryKey || rcpt.m_tripKey != m_tripKey)
            return false;

        boolean emptyCommment = false;
        if ((rcpt.m_comment == null || rcpt.m_comment.equals("")) &&
        (m_comment == null || m_comment.equals("")))
        {
            emptyCommment = true;
        }

        if (emptyCommment == false){
            if (rcpt.m_comment != m_comment && rcpt.m_comment.equals(m_comment) == false)
            return false;
        }

        if (rcpt.m_currency != m_currency && rcpt.m_currency.equals(m_currency) == false)
            return false;

        if (rcpt.m_date != m_date && rcpt.m_date.equals(m_date) == false)
            return false;

        if (rcpt.m_expenseType != m_expenseType && rcpt.m_expenseType.equals(m_expenseType) == false)
            return false;

        if (rcpt.m_photo != m_photo && rcpt.m_photo.equals(m_photo) == false)
            return false;

        return true;

    }

    void transferData(Receipt rcpt){

        m_comment = rcpt.getComment();
        m_currency = rcpt.getCurrency();
        m_date = rcpt.getDate();
        m_expenseType = rcpt.getExpenseType();
        m_photo = rcpt.getphoto();

        m_amount = rcpt.getAmount();
        m_expenseTypeOrder = rcpt.getExpenseTypeOrder();
        m_primaryKey = rcpt.getPrimaryKey();
        m_tripKey = rcpt.getTripKey();
    }

}
