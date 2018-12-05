package com.czm.module.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.czm.module.common.base.VantageBaseActivity;

public class Base64ToBitmapActivity extends VantageBaseActivity {

    private ImageView img1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base64tobitmap);
    }

    @Override
    protected void initView() {
        img1=findViewById(R.id.img1);
    }

    @Override
    protected void setEvent() {

    }

    @Override
    protected void initData() {

    }
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
