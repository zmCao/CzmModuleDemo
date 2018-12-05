package com.czm.module.customcontrol.ui_1_1.practtice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.czm.module.common.utils.Utils;
import com.czm.module.customcontrol.R;

public class Practice1DrawColorView extends View {

    public Practice1DrawColorView(Context context) {
        super(context);
    }

    public Practice1DrawColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice1DrawColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.YELLOW);
//        练习内容：使用 canvas.drawColor() 方法把 View 涂成黄色
//        黄色： Color.YELLOW
        Paint paintSRC = new Paint();
        {
            paintSRC.setColor(Color.BLUE);
            paintSRC.setAntiAlias(true);//设置抗锯齿
            paintSRC.setStyle(Paint.Style.FILL);//设置填充样式
            paintSRC.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
            paintSRC.setFilterBitmap(true);//加快显示速度，本设置项依赖于dither和xfermode的设置
        }
        Paint paintDSt = new Paint();
        {
            paintDSt.setAntiAlias(true);//设置抗锯齿
            paintDSt.setStyle(Paint.Style.FILL);//设置填充样式
            paintDSt.setColor(Color.RED);
        }
        int sc = canvas.saveLayer(null,null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(300, 300, 200, paintDSt);//目标
        paintSRC.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawRect(0, 0, 400, 400, paintSRC);   //源
        paintSRC.setXfermode(null);
        canvas.restoreToCount(sc);

    }
    public Bitmap createDstBigmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint scrPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scrPaint.setColor(0xFFFFCC44);
        canvas.drawCircle(width / 2, height / 2, width / 2, scrPaint);
        return bitmap;
    }

    public Bitmap createSrcBigmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint dstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dstPaint.setColor(0xFF66AAFF);
        canvas.drawRect(new Rect(0, 0, width, height), dstPaint);
        return bitmap;
    }
}
