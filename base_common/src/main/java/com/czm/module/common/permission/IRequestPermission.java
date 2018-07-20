package com.czm.module.common.permission;

public interface IRequestPermission {
    /**
     * 授权成功
     */
    public void onSuccess();

    /**
     * 授权失败
     */
    public void  onFailed();

    /**
     * 设置返回
     */
    public void onSettingComeback();

    /**
     * 取消设置
     */
    public void onSettingCancel();
}
