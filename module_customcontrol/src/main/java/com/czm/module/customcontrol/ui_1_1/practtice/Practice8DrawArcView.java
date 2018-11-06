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

public class Practice8DrawArcView extends View {

    public Practice8DrawArcView(Context context) {
        super(context);
    }

    public Practice8DrawArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice8DrawArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(200, 100, 800, 500, -110, 100, false, paint); // 绘制扇形
        canvas.drawArc(200, 100, 800, 500, 20, 140, true, paint); // 绘制弧形
//        练习内容：使用 canvas.drawArc() 方法画弧形和扇形
    }
}
