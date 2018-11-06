package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice10HistogramView extends View {

    public Practice10HistogramView(Context context) {
        super(context);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice10HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawLine(100,100,100,600,paint);
        canvas.drawLine(100,600,1000,600,paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(120,100,150,600,paint);
        canvas.drawRect(190,120,220,600,paint);
        canvas.drawRect(260,130,290,600,paint);
        canvas.drawRect(330,190,360,600,paint);
        canvas.drawRect(400,300,430,600,paint);
        canvas.drawText("rrr",130,650,paint);
//        综合练习
//        练习内容：使用各种 Canvas.drawXXX() 方法画直方图
    }
}
