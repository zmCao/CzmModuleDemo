package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice4DrawPointView extends View {

    public Practice4DrawPointView(Context context) {
        super(context);
    }

    public Practice4DrawPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice4DrawPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint1=new Paint();
        paint1.setColor(Color.RED);
        paint1.setStrokeWidth(30);
        paint1.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPoint(50,50,paint1);

        paint1.setStrokeCap(Paint.Cap.SQUARE);
        paint1.setStyle(Paint.Style.STROKE);//无效
        canvas.drawPoint(200,50,paint1);
//        练习内容：使用 canvas.drawPoint() 方法画点
//        一个圆点，一个方点
//        圆点和方点的切换使用 paint.setStrokeCap(cap)：`ROUND` 是圆点，`BUTT` 或 `SQUARE` 是方点
    }
}
