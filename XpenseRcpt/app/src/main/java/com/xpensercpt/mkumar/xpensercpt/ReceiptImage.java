package com.xpensercpt.mkumar.xpensercpt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        private String m_absPath;

        public int getId(){
            return m_id;
        }

        public boolean isNew(){
            return m_isNew;
        }

        public Bitmap getImage(){
            return m_image;
        }

        public String getAbsPath(){
            return m_absPath;
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

    public void load(Receipt theReceipt, Context ctxt) {
        m_imageDataArr.clear();

        String[] arr = theReceipt.getphoto().split(",");

        for (String indexStr :
                arr) {

            Bitmap myBitmap = null;
            File imgFile = theReceipt.imageFile(indexStr,ctxt);
            if (imgFile.exists()) {
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
            if (myBitmap == null)
                continue;

            ReceiptImageData data = new ReceiptImageData();
            data.m_isNew = false;
            data.m_id =  Integer.parseInt(indexStr);
            data.m_image = myBitmap;
            data.m_absPath = imgFile.getAbsolutePath();
            m_imageDataArr.add(data);
            m_nextId = data.m_id + 1;
        }

    }

    public ReceiptImageData addNewImage(Bitmap bmp, String absPath){
        ReceiptImageData data = new ReceiptImageData();
        data.m_id = m_nextId;
        m_nextId++;
        data.m_isNew = true;
        data.m_absPath = absPath;

        data.m_image = bmp;
        //int width = (int) (data.m_image.getWidth() * 0.25);
        //int height = (int) (data.m_image.getHeight() * 0.25);
        //data.m_image = Bitmap.createScaledBitmap(data.m_image, width, height, false);

        m_imageDataArr.add(data);

        return data;
    }

    public ReceiptImageData addNewImage(File photoFile){
        ReceiptImageData data = new ReceiptImageData();
        data.m_id = m_nextId;
        m_nextId++;
        data.m_isNew = true;
        data.m_absPath = photoFile.getAbsolutePath();

        data.m_image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        //int width = (int) (data.m_image.getWidth() * 0.25);
        //int height = (int) (data.m_image.getHeight() * 0.25);
        //data.m_image = Bitmap.createScaledBitmap(data.m_image, width, height, false);

        m_imageDataArr.add(data);

        return data;
    }


    public ReceiptImageData dataForId(int id){
        for (ReceiptImageData data :
                m_imageDataArr) {
            if (data.getId() == id)
                return data;
        }

        return null;
    }

    public boolean saveData(ReceiptImageData data, Receipt theReceipt, Context ctxt){
        File imgFile = theReceipt.imageFile(data.getId() + "", ctxt);
        if (imgFile.exists()) {
            boolean deleted = imgFile.delete();
            Assert.assertEquals(deleted, true);
        }

        imgFile = theReceipt.imageFile(data.getId() + "", ctxt);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imgFile);
            data.m_image.compress(Bitmap.CompressFormat.JPEG, 60, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        data.m_isNew = false;

        return true;

    }

    public boolean deleteImageAt(ReceiptImageData data){
        return m_imageDataArr.remove(data);
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
