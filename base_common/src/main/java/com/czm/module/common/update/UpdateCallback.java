package com.czm.module.common.update;

public interface UpdateCallback {
	
	/**
	 * 检查网络连接
	 */
	public void noInternet();

	/**
	 * 检查更新完成
	 * @param hasUpdate
	 * @param version
	 * @param versionCode
	 * @param newContent
	 */
	public void checkUpdateCompleted(Boolean hasUpdate, String version, int versionCode, String newContent);
	
	/**
	 * 检查更新失败
	 */
	public void checkUpdateError();	
	
	/**
	 * 更新进度
	 * @param progress
	 */
	public void downloadProgressChanged(int progress);
	
	/**
	 * 取消下载
	 */
	public void downloadCanceled();
	
	/**
	 * 完成下载
	 * @param sucess
	 * @param errorMsg
	 */
	public void downloadCompleted(Boolean sucess, CharSequence errorMsg);
}
