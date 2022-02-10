package com.ohad.smartRoommates.Utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class Utilities {


    public static String reWriteNumber(String phone) {
        char num[] = new char[phone.length()];
        for(int i = 0 ; i< phone.length() ; i++){
            num[i] = phone.charAt(i);
        }
        String newNum = "+972" ;
        for(int i = 1 ; i< phone.length() ; i++){
            newNum += phone.charAt(i);
        }
        return newNum ;
    }

    public static void clearApplicationData(AppCompatActivity activity) {
        File cache = activity.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TOHJH", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dire) {
        if ( dire != null && dire.isDirectory()) {
            String[] children = dire.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dire, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dire.delete();
    }

    public static String encodeImage(Bitmap bitmap){
        int previewWidth = 150 ;
        int previewHeight = 150 ;
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG , 50 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }


}
