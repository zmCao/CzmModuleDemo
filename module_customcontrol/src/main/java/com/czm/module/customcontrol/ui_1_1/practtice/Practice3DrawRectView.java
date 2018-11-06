package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class Practice3DrawRectView extends View {

    public Practice3DrawRectView(Context context) {
        super(context);
    }

    public Practice3DrawRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice3DrawRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint1=new Paint();
        paint1.setStyle(Paint.Style.FILL);
        canvas.drawRect(100,100,500,500,paint1);

        Paint paint2=new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        canvas.drawRect(700,100,1000,500,paint2);
//        练习内容：使用 canvas.drawRect() 方法画矩形
    }
}
