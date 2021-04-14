package cn.edu.cqu.studentmanager;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.edu.cqu.studentmanager.util.BlobUtil;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView zoomAvatar = findViewById(R.id.zoom_avatar);
        LinearLayout zoom = findViewById(R.id.zoom);
        byte [] avatarBlob = this.getIntent().getByteArrayExtra("avatar");
        Bitmap avatarBitmap = BlobUtil.blob2Bitmap(avatarBlob);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        Bitmap bitmap = alterSizeBitmap(avatarBitmap,screenWidth);

        zoomAvatar.setImageBitmap(bitmap);

        Log.i(TAG, "onCreate: height = "+bitmap.getHeight()+", width = "+bitmap.getWidth());

        zoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view == null) return false;
                finish();
                return true;
            }
        });
    }

    public Bitmap alterSizeBitmap(Bitmap bitmap, int newWidth) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = scaleWidth;
        Matrix matrix =new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
    }
}