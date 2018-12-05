package com.czm.module.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScanView  {

//    //四角
//    private int center_x,center_y,radius,MaskRadius;
//    //当前控件宽高
//    private int mWidth, mHeight;
//    //背景
//    private int backgroundColor;
//    private Paint backgroundPaint;
//    //线刷新到的Y轴位置
//    private int scanY;
//
//    // 扫描图片
//    Bitmap scanCrc,scanMaskSrc;
//    Paint paint;
//    Bitmap scanArcSrc;
//
//    Rect all,scan;
//
//    public ScanView(Context context) {
//        this(context, null);
//    }
//
//    public ScanView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public ScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        //获取自定义属性值
//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ScanView, defStyleAttr, 0);
//
//        center_y = a.getInt(R.styleable.ScanView_centerY, 0);
//        backgroundColor = a.getColor(R.styleable.ScanView_backgroundColor, Color.parseColor("#33000000"));
//
//        // 扫描图片
//        scanCrc = BitmapFactory.decodeResource(getResources(), R.mipmap.scan);
//        scanArcSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_bg);
//        scanMaskSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.scan_mask_bg);
//        radius = a.getDimensionPixelSize(R.styleable.ScanView_radius, (int) DensityUtil.dip2px(context, 10f));
//        MaskRadius=radius-(int) DensityUtil.dip2px(context, 10f);
//        //扫描线画笔
//        paint = new Paint();
//        paint.setAntiAlias(true);//设置抗锯齿
//        paint.setStyle(Paint.Style.FILL);//设置填充样式
//        paint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
//        paint.setFilterBitmap(true);//加快显示速度，本设置项依赖于dither和xfermode的设置
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = getWidth();
//        mHeight = getHeight();
//        //计算四角坐标
//        center_x=mWidth/2;
//        if (center_y==0){
//            center_y=mHeight/2;
//        }else{
//            center_y=mHeight/100*center_y;
//        }
//        //初始化画笔
//        backgroundPaint = new Paint();
//        // 去除画笔锯齿
//        backgroundPaint.setAntiAlias(true);
//        //填充
//        backgroundPaint.setStyle(Paint.Style.FILL);
//        //设置颜色
//        backgroundPaint.setColor(backgroundColor);
//
//        //线的位置默认y轴起始位置
//        scanY = center_y-MaskRadius;
//
//        all=new Rect(0,0,mWidth,mHeight);
//        scan=new Rect(center_x-radius, center_y-radius, center_x+radius, center_y+radius);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //画背景
//
//        /**
//         * 设置View的离屏缓冲。在绘图的时候新建一个“层”，所有的操作都在该层而不会影响该层以外的图像
//         * 必须设置，否则设置的PorterDuffXfermode会无效，具体原因不明
//         */
//        int sc = canvas.saveLayer(0, 0, mWidth, mHeight, paint, Canvas.ALL_SAVE_FLAG);
//        // 先绘制遮罩
//        canvas.drawRect(all, backgroundPaint);
//        paint.setColorFilter(null);
//        // 设置混合模式 （只在源图像和目标图像相交的以外绘制目标图像）
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        // 再绘制范围
//        canvas.drawCircle(center_x,center_y,MaskRadius, paint);
//        // 还原混合模式
//        paint.setXfermode(null);
//        /**
//         * 还原画布，与canvas.saveLayer配套使用
//         */
//        canvas.restoreToCount(sc);
//        // 绘制外框圆
//        canvas.drawBitmap(scanArcSrc, null, scan, paint);
//
//
//        /**
//         * 设置View的离屏缓冲。在绘图的时候新建一个“层”，所有的操作都在该层而不会影响该层以外的图像
//         * 必须设置，否则设置的PorterDuffXfermode会无效，具体原因不明
//         */
//        int sc2 = canvas.saveLayer(0, 0, mWidth, mHeight, paint, Canvas.ALL_SAVE_FLAG);
//        paint.setColorFilter(null);
//        // 先绘制扫描图
//        //裁切部分裁掉多余部分（Y轴上部去除，这部分配合下面的绘制区域使用）
//        Rect cutting=new Rect(0, (int)(((2*MaskRadius)-(scanY-center_y+(float)MaskRadius))/(2*MaskRadius)*scanCrc.getHeight()), scanCrc.getWidth(), scanCrc.getHeight());
////        Log.i("test",""+(int)((scanY-center_y+(float)MaskRadius)/(2*MaskRadius)*scanCrc.getHeight())+"\n(scanY-center_y+MaskRadius)"+(scanY-center_y+MaskRadius));
//        canvas.drawBitmap(scanCrc, cutting, new Rect(center_x-MaskRadius, center_y-MaskRadius, center_x+MaskRadius, scanY), paint);
//        // 设置混合模式 （只在源图像和目标图像相交的地方绘制目标图像）
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        // 再绘制范围
//        canvas.drawBitmap(scanMaskSrc, null, scan, paint);
//        // 还原混合模式
//        paint.setXfermode(null);
//        /**
//         * 还原画布，与canvas.saveLayer配套使用
//         */
//        canvas.restoreToCount(sc2);
//
//        //线不断往下
//        if (scanY < center_y+MaskRadius) {
//            scanY += 4;
//        } else {
//            scanY = center_y-MaskRadius;
//        }
//        invalidate();
//    }
}
