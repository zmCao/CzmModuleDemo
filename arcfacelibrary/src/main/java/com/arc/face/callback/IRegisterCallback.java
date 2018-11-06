package com.arc.face.callback;

import android.graphics.Bitmap;

/**
 * 人脸注册回调
 */
public interface IRegisterCallback {
    /**
     * 检测成功
     * @param bitmap 人脸
     */
    public void ok(Bitmap bitmap);

    /**
     * 无人脸特征值
     */
    public void no_feature();

    /**
     * 无人脸
     */
    public void no_face();

    /**
     * 人脸检测API初始化失败
     */
    public void fd_error();

    /**
     * 人脸比对API初始化失败
     */
    public void fr_error();
}
