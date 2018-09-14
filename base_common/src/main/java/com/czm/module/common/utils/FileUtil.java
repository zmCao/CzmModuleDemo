package com.czm.module.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by zhuyf on 2015/12/14.
 */
public class FileUtil {
    /**
     * 返回文件扩展名
     *
     * @param Path
     * @return
     */
    public static String GetExt(String Path) {
        if (TextUtils.isEmpty(Path) || Path.indexOf(".") == -1) {
            return "";
        } else {
            return Path.substring(Path.lastIndexOf("."));
        }
    }

    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }


    /**
     * 获取图库中图片路径（4.4得到的uri,需要以下方法来获取文件的路径）
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    public static String getPhotoPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File creatFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建文件夹
     *
     * @param dirName
     * @return
     */
    public static File creatDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }


    /**
     * 删除某一目录下的所文件
     *
     * @param DelDir
     */
    public static void DelFileInDIR(String DelDir) {
        File dir = new File(DelDir);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 保存图片
     * @param b
     * @param Folder
     * @param FileName
     */
//    public static void savePic(Bitmap b, String Folder,String FileName) {
//        FileOutputStream fos = null;
//        try {
//            File cacheDir = new File(Folder);
//            if(!cacheDir.exists())
//            {
//                cacheDir.mkdirs();
//            }
//            fos = new FileOutputStream(Folder+FileName);
//            if (null != fos) {
//                byte[] newbtArr=compressPic(b,960);
//                fos.write(newbtArr);
//                fos.flush();
//                fos.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 图片体积压缩
     * @param image
     * @param maxVolume 压缩的体积大小单位K
     * @return
     */
//    public static byte[] compressPic(Bitmap image,int maxVolume)
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        int options = 100;
//        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//        while ((baos.toByteArray().length * 1.5) / 1024 > maxVolume) {
//            baos.reset();
//            options -= 10;//每次都减少10
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            if(options<=20)
//            {
//                break;
//            }
//        }
//        return baos.toByteArray();
//    }
    /**
     * imgView横屏填满，高度根据比例计算
     * @param mView imgView
     * @param HWScale 高宽比例
     */
//    public static void SetImageHeight(View mView,double HWScale)
//    {
//        int iViewWith=mView.getWidth();
//        ViewGroup.LayoutParams parmas;
//        parmas=mView.getLayoutParams();
//        parmas.height=(int) (iViewWith*HWScale);
//        mView.setLayoutParams(parmas);
//    }

    /**
     * 根据比例缩放
     * @param bitmap
     * @param wScale
     * @param hScale
     * @return
     */
//    public static Bitmap zoomBitmap(Bitmap bitmap,float wScale,float hScale) {
//        Matrix matrix = new Matrix();
//        matrix.postScale(wScale,hScale); //长和宽放大缩小的比例
//        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//        bitmap.recycle();
//        return resizeBmp;
//    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 透明度0.0-1.0
     */
    public static void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readAssetFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取asset目录下音频文件。
     *
     * @return 二进制文件数据
     */
    public static byte[] readAssetAudioFile(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 保存下载的图片流写入SD卡文件
     *  R
     * @param path      路径
     * @param imageName xxx.jpg
     * @param body      image stream
     */
    public static void writeResponseBodyToDisk(String path, String imageName, ResponseBody body) {
        try {
            InputStream is = body.byteStream();
            File fileDr = new File(path);
            if (!fileDr.exists()) {
                fileDr.mkdir();
            }
            File file = new File(path, imageName);
            if (file.exists()) {
                file.delete();
                file = new File(path, imageName);
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
