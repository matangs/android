package com.xpensercpt.mkumar.xpensercpt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mkumar on 10/10/15.
 * Helper class to keep list of images that were added new or should be deleted.
 */
public class ReceiptImage {

    public class ReceiptImageData{
        private Bitmap m_image;
        private boolean m_isNew;
        private int m_id;

        public int getId(){
            return m_id;
        }

        public boolean isNew(){
            return m_isNew;
        }

        public Bitmap getImage(){
            return m_image;
        }

    }

    private ArrayList<ReceiptImageData> m_imageDataArr;
    private int m_nextId;

    public ReceiptImage(){
        m_imageDataArr = new ArrayList<>();
    }

    public ArrayList<ReceiptImageData> getImageDataArr(){
        return m_imageDataArr;
    }

    public int getNextId(){
        return m_nextId;
    }

    public void load(String photoStr, Receipt theReceipt, Context ctxt) {
        m_imageDataArr.clear();

        String[] arr = photoStr.split(",");

        for (String indexStr :
                arr) {

            Bitmap myBitmap = null;
            File imgFile = theReceipt.imageFile(indexStr,ctxt);
            if(imgFile.exists()) {
                String str = imgFile.getAbsolutePath();
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            if (myBitmap == null)
                continue;

            ReceiptImageData data = new ReceiptImageData();
            data.m_isNew = false;
            data.m_id =  Integer.parseInt(indexStr);
            data.m_image = myBitmap;
            m_imageDataArr.add(data);
            m_nextId = data.m_id + 1;
        }

    }

    public void addNewImage(Bitmap image){
        ReceiptImageData data = new ReceiptImageData();

        data.m_image = image;
        data.m_isNew = true;
        data.m_id = m_nextId;
        m_imageDataArr.add(data);
        m_nextId++;
    }

    public boolean deleteImageAt(Receipt theReceipt, Context ctxt, int index) {
        m_imageDataArr.remove(index);
        File imgFile = theReceipt.imageFile(index + "", ctxt);
        return imgFile.exists() && imgFile.delete();
    }

    public String getPhotoStr(){

        int size = m_imageDataArr.size();
        String finalStr = "";
        for (int i = 0; i < size; i++){
            if (i >= size-1)
                finalStr = finalStr + m_imageDataArr.get(i).m_id;
            else
                finalStr = finalStr + m_imageDataArr.get(i).m_id + ",";
        }

        return finalStr;
    }
}
