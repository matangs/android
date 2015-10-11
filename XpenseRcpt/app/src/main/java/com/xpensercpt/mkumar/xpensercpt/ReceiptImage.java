package com.xpensercpt.mkumar.xpensercpt;

import java.util.ArrayList;

/**
 * Created by mkumar on 10/10/15.
 * Helper class to keep list of images that were added new or should be deleted.
 */
public class ReceiptImage {

    class ReceiptImageData{
        /*private UIImage m_image;
        */
        private boolean m_isNew;
        private int m_id;

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

    public void load(String photoStr, Receipt theReceipt) {
        String[] arr = photoStr.split(",");

        for (String indexStr :
                arr) {

            ReceiptImageData data = new ReceiptImageData();

            /* load image once you have figured out

            String imgPath = theReceipt.imagePath(indexStr);

            UIImage* img = [UIImage imageWithContentsOfFile:imgPath];
            UIImageOrientation orientation = img.imageOrientation;
            if (orientation != UIImageOrientationUp){
                data.m_image = [UIImage imageWithCGImage:[img CGImage]
                scale:1.0
                orientation: UIImageOrientationUp];
            }
            else
                data.m_image = img;
               */

            data.m_isNew = false;
            data.m_id =  Integer.parseInt(indexStr);
            m_imageDataArr.add(data);
            m_nextId = data.m_id + 1;
        }

    }

    public void addNewImage(/*UIImage image*/){
        ReceiptImageData data = new ReceiptImageData();

        //data.m_image = image;
        data.m_isNew = true;
        data.m_id = m_nextId;
        m_imageDataArr.add(data);
        m_nextId++;
    }

    public void deleteImageAt(int index){
        m_imageDataArr.remove(index);
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
