package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice2DrawCircleView extends View {

    public Practice2DrawCircleView(Context context) {
        super(context);
    }

    public Practice2DrawCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice2DrawCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        练习内容：使用 canvas.drawCircle() 方法画圆
//        一共四个圆：1.实心圆 2.空心圆 3.蓝色实心圆 4.线宽为 20 的空心圆
        Paint paint1=new Paint();
        paint1.setColor(Color.RED);
        canvas.drawCircle(100,100,50,paint1);

        Paint paint2=new Paint();
        paint2.setColor(Color.RED);
        paint2.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(300,100,50,paint2);

        Paint paint3=new Paint();
        paint3.setColor(Color.RED);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(20);
        canvas.drawCircle(500,100,50,paint3);

        Paint paint4=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint4.setColor(Color.RED);
        canvas.drawCircle(100,300,50,paint4);
    }
}
