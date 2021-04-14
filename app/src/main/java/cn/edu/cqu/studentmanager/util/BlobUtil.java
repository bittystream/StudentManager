package cn.edu.cqu.studentmanager.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BlobUtil {
    public static byte[] bitmap2Blob(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }

    public static Bitmap blob2Bitmap(byte [] b){
        if (b.length != 0) return BitmapFactory.decodeByteArray(b,0,b.length);
        return null;
    }
}
