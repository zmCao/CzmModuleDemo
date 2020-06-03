package com.czm.module.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by czm on 2015/12/14.
 */
public class BitMapUtil {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    /**
     * 判断是否为图片
     *
     * @return
     */
    public static boolean IsImage(String sParam) {
        String sExt = FileUtil.GetExt(sParam);
        String[] IMAGE = {".jpg", ".jpeg", ".gif", ".bmp", ".png", ".image", ".thumb", ".thumb1", ".thumb2", ".thumb3"};
        boolean bOk = false;
        for (String aIMAGE : IMAGE) {
            if (aIMAGE.equals(sExt)) {
                bOk = true;
                break;
            }
        }
        return bOk;
    }

    /*
     * 获取文件名
     */
    public static String GetFileName(String FullName) {
        return FullName.substring(0, FullName.lastIndexOf("."));
    }

    /**
     * 根据Base64字符串获得安全的图片，防止出现内存溢出异常
     *
     * @return
     */
    public static Bitmap GetSafeBitmapBy64(String strBase64, int maxNumOfPixels) {
        Bitmap bitmap = null;
        BitmapFactory.Options newOpts = null;
        try {
            newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了,自动缩放 inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度
            newOpts.inJustDecodeBounds = true;
            byte[] btImage = Base64.decode(strBase64, Base64.DEFAULT);
            newOpts.inSampleSize = computeSampleSize(newOpts, -1, maxNumOfPixels);
            newOpts.inJustDecodeBounds = false;
            newOpts.inPreferredConfig = Config.RGB_565;
            bitmap = BitmapFactory.decodeByteArray(btImage, 0, btImage.length);
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
        }
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        //上下限范围
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * imgView横屏填满，高度根据比例计算
     *
     * @param mView   imgView
     * @param HWScale 高宽比例
     */
    public static void SetImageHeight(View mView, double HWScale) {
        int iViewWith = mView.getWidth();
        LayoutParams parmas;
        parmas = mView.getLayoutParams();
        parmas.height = (int) (iViewWith * HWScale);
        mView.setLayoutParams(parmas);
    }

    /**
     * 获取Assets文件中图片
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 根据比例缩放
     *
     * @param bitmap
     * @param wScale
     * @param hScale
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float wScale, float hScale) {
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizeBmp;
    }

    /**
     * 保存图片
     *
     * @param b
     * @param Folder
     * @param FileName
     */
    public static void savePic(Bitmap b, String Folder, String FileName) {
        FileOutputStream fos = null;
        try {
            File cacheDir = new File(Folder);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            fos = new FileOutputStream(Folder + FileName);
            if (null != fos) {
                byte[] newbtArr = compressPic(b, 100);
                fos.write(newbtArr);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩尺寸
     *
     * @param srcPath
     * @param MaxLength 最大边的长度
     * @return
     */
    public static Bitmap getMaxImage(String srcPath, int MaxLength, int maxVolume) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
            int w = newOpts.outWidth;//宽度
            int h = newOpts.outHeight;//高度
            //大小压缩  缩到相应大小灵活
            //最大边的基准长度
            float ww = 960f;
            float be = 1.0f;
            if (w > h) {
                be = w / ww;
                if (be > 1) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, MaxLength, Math.round(h / be), true);//缩放图片
                }
            } else {
                be = h / ww;
                if (be > 1) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(w / be), MaxLength, true);//缩放图片
                }
            }
            //质量压缩
            byte[] buffer = compressPic(bitmap, maxVolume);// 压缩好比例大小后再进行质量压缩
            ByteArrayInputStream isBm = new ByteArrayInputStream(buffer);// 把压缩后的数据baos存放到ByteArrayInputStream中
            bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception ex) {
            Log.i("MyLogCat", "尺寸压缩出错" + ex.getMessage());
        }
        return bitmap;
    }

    /**
     * 返回BitMap的字节数组
     *
     * @param image
     * @return
     */
    public static byte[] getBitMap(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 图片体积压缩
     *
     * @param image
     * @param maxVolume 压缩的体积大小单位K
     * @return
     */
    public static byte[] compressPic(Bitmap image, int maxVolume) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while ((baos.toByteArray().length * 1.5) / 1024 > maxVolume) {
            baos.reset();
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            if (options <= 20) {
                break;
            }
        }
        return baos.toByteArray();
    }

    /**
     * 生成缩略图
     * 根据路径获得安全的图片，（防止出现内存溢出异常）
     *
     * @param Path
     * @return
     */
    public static Bitmap GetSafeBitmap(String Path, int maxNumOfPixels) {
        Bitmap bitmap = null;
        BitmapFactory.Options newOpts = null;
        //保存到缩略图目录下
        try {
            File mfile = new File(Path);
            if (mfile.exists()) //若该文件存在
            {
                newOpts = new BitmapFactory.Options();
                // 开始读入图片，此时把options.inJustDecodeBounds 设回true了,自动缩放 inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度
                newOpts.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeFile(Path, newOpts);// 此时返回bm为空
                newOpts.inSampleSize = computeSampleSize(newOpts, -1, maxNumOfPixels);
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Config.RGB_565;
                newOpts.inPurgeable = true;
                // 位图可以共享一个参考输入数据(inputstream、阵列等)
                newOpts.inInputShareable = true;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(mfile), null, newOpts);
                //bitmap = BitmapFactory.decodeFile(Path, newOpts);
            }
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 根据路径和文件名获得图片
     *
     * @param sDir
     * @param sFileName
     * @return
     */
    public static Bitmap GetLocalPic(String sDir, String sFileName) {
        Bitmap oBitmap = null;
        File fcache = new File(sDir + sFileName);
        if (fcache.exists()) {
            try {
                oBitmap = BitmapFactory.decodeStream(new FileInputStream(sDir + sFileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return oBitmap;
    }

    /**
     * 保存缓存图片
     *
     * @param fileName
     * @param picext
     * @param bitmap
     * @param folderCache
     * @throws IOException
     */
    public static void savaCacheBitmap(String fileName, String filePath, Bitmap.CompressFormat picext, Bitmap bitmap, String folderCache) throws IOException {

        File cacheDir = new File(filePath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        File saveBitmap = new File(filePath + folderCache + fileName);
        FileOutputStream fos = new FileOutputStream(saveBitmap);
        if (null != fos) {
            bitmap.compress(picext, 100, fos);
            fos.flush();
            fos.close();
        }
    }

    /**
     * 获得图片的缩略图并保存到目录中
     *
     * @param Path          原图存放路径
     * @param ThumbnailPath 所略图存放路径
     * @param FileName      文件名 不包含文件扩展名
     */
    public static void SaveThumbnail(String Path, String ThumbnailPath, String FileName) {
        //BitmapDrawable oBitDraw = new BitmapDrawable(Path);
        //Bitmap bitmap = oBitDraw.getBitmap();
        Bitmap bitmap = null;
        //保存到缩略图目录下
        try {
            bitmap = GetSafeBitmap(Path + FileName + ".image", 128 * 128);
            if (bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 170, 255);//获取缩略图
                //新建存放缩略图的目录
                File Thumbnaidir = new File(ThumbnailPath);
                if (!Thumbnaidir.exists())
                    Thumbnaidir.mkdirs();
                File Thumbnaifile = null;
                if (Path.equals(ThumbnailPath)) {
                    //缩略图和原图在一个路径下
                    Thumbnaifile = new File(ThumbnailPath + FileName + ".thumb2");
                } else {
                    Thumbnaifile = new File(ThumbnailPath + FileName + ".thumb2");
                }
                FileOutputStream fos = new FileOutputStream(Thumbnaifile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException | OutOfMemoryError er) {
            er.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 移动文件
     *
     * @param SourceFullPath 源文件
     * @param DestFullPath   目标文件
     * @param IsCopy         复制或前切
     */
    public static void MovePIC(String SourceFullPath, String DestFullPath, boolean IsCopy) {
        try {
            File desDir = new File(DestFullPath.substring(0, DestFullPath.lastIndexOf("/") + 1));
            if (!desDir.exists()) {
                desDir.mkdirs();
            }
            Bitmap sourceBitmap = getMaxImage(SourceFullPath, 960, 100);
            if (sourceBitmap != null) {
                //判断图片旋转角度
                int degree = readPictureDegree(SourceFullPath);
                //旋转角度
                if (degree != 0) {
                    sourceBitmap = rotaingImageView(degree, sourceBitmap);
                }
                //压缩后再移动
                byte[] compressByte = compressPic(sourceBitmap, 100);
                //剪切
                File dstiFile = new File(DestFullPath);
                FileOutputStream fos = new FileOutputStream(dstiFile);
                fos.write(compressByte, 0, compressByte.length);
                if (!IsCopy) {
                    File sFile = new File(SourceFullPath);
                    sFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断图片选择的角度
     *
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL: {
                    degree = 0;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    degree = 90;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    degree = 180;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    degree = 270;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * View转换为Bitmap
     *
     * @param v View
     * @return Bitmap
     */
    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 截图listview
     **/
    public static Bitmap createViewBitmap(ListView listView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        return bitmap;
    }

    /**
     * 截图webview  已兼容API 19，注意主线程中使用webView.getContentHeight()
     *
     * @param webView
     * @return
     */
    public static Bitmap createViewBitmap(Context mContext, final WebView webView) {
        Bitmap bitmap = null;
//		Picture picture = webView.capturePicture(); android4.4 废弃
//		int width = (int) MobileUtil.dip2px(mContext, picture.getWidth()) ;
//		int height = (int) MobileUtil.dip2px(mContext, picture.getHeight());
        int width = webView.getWidth(); //获取单位 px
        int height = (int) MobileUtil.dip2px(mContext, webView.getContentHeight()); //获取单位dp
        if (width > 0 && height > 0) {
            bitmap = Bitmap.createBitmap(width, height,
                    Config.ARGB_8888);
        }
        final Canvas canvas = new Canvas(bitmap);
        webView.draw(canvas);
        return bitmap;
    }

    /**
     * 截取scrollview的测量屏幕
     **/
    public static Bitmap createViewBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        if (h < scrollView.getHeight())
            h = scrollView.getHeight();
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 2个 Bitmap 竖着 合并
     *
     * @param b1       上面的图片
     * @param b2       下面的图片
     * @param activity
     * @param color    颜色值 例如：0xFFFFFFFF
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap b1, Bitmap b2, Activity activity, int color) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        Bitmap bitmap = null;
        if (b1 != null && b2 != null) {

            int h1 = b1.getHeight();
            int h2 = b2.getHeight();
            int w1 = b1.getWidth();
            int w2 = b2.getWidth();
            if (screenHeigh > h1 + h2)
                bitmap = Bitmap.createBitmap(w1, screenHeigh, Config.ARGB_8888);
            else
                bitmap = Bitmap.createBitmap(w1, h1 + h2, Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(color);
            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, h1, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }

    /**
     * 多个Bitmap竖着合并（宽度一样的Bitmap）
     *
     * @param mBitmapList
     * @param activity
     * @param color       颜色值 例如：0xFFFFFFFF
     * @return Bitmap
     */
    public static Bitmap combineBitmap(List<Bitmap> mBitmapList, Activity activity, int color) {
        int h = 0;
        int w = 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        Bitmap bitmap = null;
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (mBitmapList.get(i) != null) {
                h += mBitmapList.get(i).getHeight();
                if (mBitmapList.get(i).getWidth() > w) {
                    w = mBitmapList.get(i).getWidth();
                }
            }
        }

        if (screenHeigh > h)
            bitmap = Bitmap.createBitmap(w, screenHeigh, Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (i == 0)
                canvas.drawBitmap(mBitmapList.get(i), 0, 0, null);
            else if (i > 0) {
                int top = 0;
                for (int j = 0; j < i; j++) {
                    top += mBitmapList.get(j).getHeight();
                }
                canvas.drawBitmap(mBitmapList.get(i), 0, top, null);
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 多个Bitmap竖着合并（宽度一样的Bitmap）
     *
     * @param mBitmapList
     * @param activity
     * @param backgroundBitmap 画布背景图片
     * @return Bitmap
     */
    public static Bitmap combineBitmap(List<Bitmap> mBitmapList, Activity activity, Bitmap backgroundBitmap) {
        int h = 0;
        int w = 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        Bitmap bitmap = null;
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (mBitmapList.get(i) != null) {
                h += mBitmapList.get(i).getHeight();
                if (mBitmapList.get(i).getWidth() > w) {
                    w = mBitmapList.get(i).getWidth();
                }
            }
        }

        if (screenHeigh > h)
            bitmap = Bitmap.createBitmap(w, screenHeigh, Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, new Paint());
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (i == 0)
                canvas.drawBitmap(mBitmapList.get(i), 0, 0, null);
            else if (i > 0) {
                int top = 0;
                for (int j = 0; j < i; j++) {
                    top += mBitmapList.get(j).getHeight();
                }
                canvas.drawBitmap(mBitmapList.get(i), 0, top, null);
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 多个Bitmap竖着合并（宽度一样的Bitmap）
     *
     * @param mBitmapList
     * @param activity
     * @param color         颜色值 例如：0xFFFFFFFF
     * @param custmerHeight 合并的高度，不与屏幕高度做对比;true不对比，false对比
     * @return Bitmap
     */
    public static Bitmap combineBitmap(List<Bitmap> mBitmapList, Activity activity, int color, boolean custmerHeight) {
        int h = 0;
        int w = 0;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        Bitmap bitmap = null;
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (mBitmapList.get(i) != null) {
                h += mBitmapList.get(i).getHeight();
                if (mBitmapList.get(i).getWidth() > w) {
                    w = mBitmapList.get(i).getWidth();
                }
            }
        }

        if (screenHeigh > h && !custmerHeight)
            bitmap = Bitmap.createBitmap(w, screenHeigh, Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        for (int i = 0; i < mBitmapList.size(); i++) {
            if (i == 0)
                canvas.drawBitmap(mBitmapList.get(i), 0, 0, null);
            else if (i > 0) {
                int top = 0;
                for (int j = 0; j < i; j++) {
                    top += mBitmapList.get(j).getHeight();
                }
                canvas.drawBitmap(mBitmapList.get(i), 0, top, null);
            }
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }
    /**
     * //通知相册更新
     */
    public static void refreshPhoto(Context mContext, Bitmap b, String Folder, String FileName) {
        File file = new File(Folder + FileName);
        MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b, file.toString(), null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
        Toast.makeText(mContext, "图片保存成功", Toast.LENGTH_SHORT).show();
    }
    /**
     * //通知相册更新
     */
    public static void refreshPhoto(Context mContext, String Folder, String FileName) {
        File file = new File(Folder + FileName);
        MediaStore.Images.Media.insertImage(mContext.getContentResolver(),BitmapFactory.decodeFile(file.getAbsolutePath()), file.toString(), null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
        Toast.makeText(mContext, "图片保存成功", Toast.LENGTH_SHORT).show();
    }
}
