package com.xpensercpt.mkumar.xpensercpt;

import android.content.Context;

import java.io.File;

/**
 * Created by mkumar on 10/10/15.
 * Helper class which can be used anywhere
 */
public class RcptHelper {

    static public void createTripDirectory(Context ctxt, int tripId){
        File f = new File(ctxt.getExternalFilesDir(null), "" + tripId);
        if (!f.exists()) {
            boolean test = f.mkdirs();
            assert  test;

        }
    }

    static public void deleteTripFolder(Context ctxt, int tripId){
        File file = new File(ctxt.getExternalFilesDir(null), "" + tripId);
        if (file.isDirectory() && file.exists()){
            boolean test = file.delete();
            assert test;
        }
    }

    static public void deleteReceiptImage(Context ctxt, int tripId, String imgName){
        File file = new File(ctxt.getExternalFilesDir(null) + "/" + tripId, imgName);
        if (file.exists()){
            boolean test = file.delete();
            assert test;
        }
    }

    static public boolean hasReceiptImage(Context ctxt, int tripId, String imgName) {
        File file = new File(ctxt.getExternalFilesDir(null) + "/" + tripId, imgName);
        return file.exists();
    }


}
