package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

public class Practice7DrawRoundRectView extends View {

    public Practice7DrawRoundRectView(Context context) {
        super(context);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice7DrawRoundRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(100, 100, 500, 300, 50, 50, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawRoundRect(600, 100, 1000, 800, 50, 50, paint);
        //        练习内容：使用 canvas.drawRoundRect() 方法画圆角矩形
    }
}
