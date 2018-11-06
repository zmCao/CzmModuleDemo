package com.arc.face.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.arc.face.callback.IRegisterCallback;
import com.arc.face.data.FaceDB;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.czm.module.common.utils.BitMapUtil;
import com.guo.android_extend.image.ImageConverter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 人脸检测注册工具类
 */
public class FaceRegisterUtil {
    private static volatile FaceRegisterUtil instance = null;

    private AFD_FSDKEngine afd_fsdkEngine = new AFD_FSDKEngine();
    private AFD_FSDKVersion afd_fsdkVersion = new AFD_FSDKVersion();
    private AFD_FSDKError afd_fsdkError;
    private AFD_FSDKFace afd_fsdkFace=new AFD_FSDKFace();
    private List<AFD_FSDKFace> afd_fsdkFaceList = new ArrayList<>();
    private AFR_FSDKVersion afr_fsdkVersion = new AFR_FSDKVersion();
    private AFR_FSDKEngine afr_fsdkEngine = new AFR_FSDKEngine();
    private AFR_FSDKFace afr_fsdkFace = new AFR_FSDKFace();
    private AFR_FSDKError afr_fsdkError;
    private Bitmap mBitmap;
    private IRegisterCallback iRegisterCallback;
    private byte[] bitmapData;
    private Rect src = new Rect();
    private Rect dst = new Rect();

    public static FaceRegisterUtil getInstance() {
        if (instance == null) {
            synchronized (FaceRegisterUtil.class) {
                if (instance == null) {
                    instance = new FaceRegisterUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 把图片转换成NV21格式
     *
     * @param imgFilePath 图片路径
     */
    public boolean convertImgToNV21(String imgFilePath) {
        boolean result = false;
        mBitmap = decodeImage(imgFilePath);
        if (IsONumber(mBitmap.getHeight())) {
            byte[] bitmapData = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
            try {
                ImageConverter convert = new ImageConverter();
                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                result = convert.convert(mBitmap, bitmapData);
                convert.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            result = false;
        return result;
    }

    public void registerFace(IRegisterCallback iRegisterCallback, SurfaceHolder surfaceHolder, boolean isShowFaceRect) {
        this.iRegisterCallback = iRegisterCallback;
        afd_fsdkError = afd_fsdkEngine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Logger.d("AFD_FSDK_InitialFaceEngine = " + afd_fsdkError.getCode());
        if (afd_fsdkError.getCode() != AFD_FSDKError.MOK) {
            iRegisterCallback.fd_error();
        }
        afd_fsdkError = afd_fsdkEngine.AFD_FSDK_GetVersion(afd_fsdkVersion);
        Logger.d("AFD_FSDK_GetVersion =" + afd_fsdkVersion.toString() + ", " + afd_fsdkError.getCode());
        afd_fsdkError = afd_fsdkEngine.AFD_FSDK_StillImageFaceDetection(bitmapData, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, afd_fsdkFaceList);
        Logger.d("AFD_FSDK_StillImageFaceDetection =" + afd_fsdkError.getCode() + "<" + afd_fsdkFaceList.size());
        while (surfaceHolder != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                Paint mPaint = new Paint();
                boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height();
                float scale;
                if (fit_horizontal) {
                    scale = canvas.getWidth() / (float) src.width();
                    dst.left = 0;
                    dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                    dst.right = dst.left + canvas.getWidth();
                    dst.bottom = dst.top + (int) (src.height() * scale);
                } else {
                    scale = canvas.getHeight() / (float) src.height();
                    dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                    dst.top = 0;
                    dst.right = dst.left + (int) (src.width() * scale);
                    dst.bottom = dst.top + canvas.getHeight();
                }
                canvas.drawBitmap(mBitmap, src, dst, mPaint);
                canvas.save();
                canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
                canvas.translate(dst.left / scale, dst.top / scale);
                if (isShowFaceRect) {
                    for (AFD_FSDKFace face : afd_fsdkFaceList) {
                        mPaint.setColor(Color.RED);
                        mPaint.setStrokeWidth(10.0f);
                        mPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(face.getRect(), mPaint);
                    }
                }
                canvas.restore();
                surfaceHolder.unlockCanvasAndPost(canvas);
                break;
            }
        }

        if (!afd_fsdkFaceList.isEmpty()) {
            afr_fsdkError = afr_fsdkEngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Logger.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + afr_fsdkError.getCode());
            if (afr_fsdkError.getCode() != AFD_FSDKError.MOK) {
               iRegisterCallback.fr_error();
            }
            afr_fsdkError = afr_fsdkEngine.AFR_FSDK_GetVersion(afr_fsdkVersion);
            Logger.d("com.arcsoft", "FR=" + afr_fsdkVersion.toString() + "," + afr_fsdkError.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
            afr_fsdkError = afr_fsdkEngine.AFR_FSDK_ExtractFRFeature(bitmapData, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(afd_fsdkFaceList.get(0).getRect()), afd_fsdkFaceList.get(0).getDegree(), afr_fsdkFace);
            Log.d("com.arcsoft", "Face=" + afr_fsdkFace.getFeatureData()[0] + "," + afr_fsdkFace.getFeatureData()[1] + "," + afr_fsdkFace.getFeatureData()[2] + "," + afr_fsdkError.getCode());
            if (afr_fsdkError.getCode() == afr_fsdkError.MOK) {
                int width = afd_fsdkFaceList.get(0).getRect().width();
                int height = afd_fsdkFaceList.get(0).getRect().height();
                Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Canvas face_canvas = new Canvas(face_bitmap);
                face_canvas.drawBitmap(mBitmap, afd_fsdkFaceList.get(0).getRect(), new Rect(0, 0, width, height), null);
               iRegisterCallback.ok(face_bitmap);
            } else {
               iRegisterCallback.no_feature();
            }
            afr_fsdkError = afr_fsdkEngine.AFR_FSDK_UninitialEngine();
            Logger.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + afr_fsdkError.getCode());
        } else {
            iRegisterCallback.no_face();
        }
        afd_fsdkError = afd_fsdkEngine.AFD_FSDK_UninitialFaceEngine();
        Logger.d( "AFD_FSDK_UninitialFaceEngine =" + afd_fsdkError.getCode());
    }

    /**
     * 判断是否为偶数
     *
     * @param n
     * @return true 偶 false 寄
     */
    private boolean IsONumber(int n) {
        return n % 2 == 0;
    }

    /**
     * 转换成Bitmap(旋转)
     *
     * @param path 图片路径
     * @return Bitmap
     */
    private Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            res = BitmapFactory.decodeFile(path, op);
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
