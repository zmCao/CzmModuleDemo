package com.czm.module.common.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class AlertDialogFragment extends DialogFragment {
	
	int mIcon;
	String mTitle;
	String mMessage;
	boolean bDisableBack=false, bEnableOK=true, bEnableCancel=true;
	static View mContentView;
	static DialogOnClickListener mOnClickListener;
    static String sOK="确认",sCancel="取消";
	
	/**
	 * 创建 AlertDialogFragment
	 * @param icon
	 * @param title
	 * @param message
	 * @param view
	 * @param onClickListener
	 * @return
	 */
	public static AlertDialogFragment newInstance(int icon, String title, String message, View view, DialogOnClickListener onClickListener) {
		AlertDialogFragment f=new AlertDialogFragment();
		mOnClickListener = onClickListener;
		mContentView = view;
		Bundle args = new Bundle();
		args.putInt("icon", icon);
		args.putString("title", title);
		args.putString("message", message);
		f.setArguments(args);
		return f;
	}
	/**
	 *  创建 AlertDialogFragment
	 * @param icon
	 * @param title
	 * @param message
	 * @param view
	 * @param DisableBack false:不禁用back键；ture:禁用back键
	 * @param EnableOK
	 * @param EnableCancel
	 * @param onClickListener
	 * @return
	 */
	public static AlertDialogFragment newInstance(int icon, String title, String message, View view, boolean DisableBack, boolean EnableOK, boolean EnableCancel, DialogOnClickListener onClickListener) {
		AlertDialogFragment f=new AlertDialogFragment();
		mOnClickListener = onClickListener;
		mContentView = view;
		Bundle args = new Bundle();
		args.putInt("icon", icon);
		args.putString("title", title);
		args.putString("message", message);
		args.putBoolean("DisableBack", DisableBack);
		args.putBoolean("EnableOK", EnableOK);
		args.putBoolean("EnableCancel", EnableCancel);
		f.setArguments(args);
        sOK="确认";
        sCancel="取消";
		return f;
	}
    /**
     *  创建 AlertDialogFragment
     * @param icon
     * @param title
     * @param message
     * @param view
     * @param DisableBack false:不禁用back键；ture:禁用back键
     * @param EnableOK
     * @param EnableCancel
     * @param ok  确定按钮文字
     * @param Cancel 取消按钮文字
     * @param onClickListener
     * @return
     */
    public static AlertDialogFragment newInstance(int icon, String title, String message, View view, boolean DisableBack, boolean EnableOK
            , boolean EnableCancel, String ok, String Cancel, DialogOnClickListener onClickListener) {
        AlertDialogFragment f=new AlertDialogFragment();
        mOnClickListener = onClickListener;
        mContentView = view;
        Bundle args = new Bundle();
        args.putInt("icon", icon);
        args.putString("title", title);
        args.putString("message", message);
        args.putBoolean("DisableBack", DisableBack);
        args.putBoolean("EnableOK", EnableOK);
        args.putBoolean("EnableCancel", EnableCancel);
        f.setArguments(args);
        sOK=ok;
        sCancel=Cancel;
        return f;
    }
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		setCancelable(false);
		mIcon = getArguments().getInt("icon");
		mTitle = getArguments().getString("title");
		mMessage = getArguments().getString("message");
		if(getArguments().containsKey("DisableBack"))
			bDisableBack=getArguments().getBoolean("DisableBack");
		if(getArguments().containsKey("EnableOK"))
			bEnableOK=getArguments().getBoolean("EnableOK");
		if(getArguments().containsKey("EnableCancel"))
			bEnableCancel=getArguments().getBoolean("EnableCancel");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		if(mIcon > 0){
			builder.setIcon(mIcon);
		}
		
		if(mTitle != null){
			builder.setTitle(mTitle);
		}
		
		if(mMessage != null){
			builder.setMessage(mMessage);
			
		}
		if(mContentView!=null){
			builder.setView(mContentView);
		}
		
		if(mOnClickListener != null){
			if(bEnableOK)
			{
				builder.setPositiveButton(sOK,
		            new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int whichButton) {
		                	if(mOnClickListener != null){
		                		mOnClickListener.onPositiveClick();
		                	}
		                }
		            }
			     );
			}
			if(bEnableCancel)
		     builder.setNegativeButton(sCancel,
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	if(mOnClickListener != null){
	                		mOnClickListener.onNegativeClick();
	                	}
	                }
	            }
		    );
		}
		if(bDisableBack)
		{
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
		}
		return builder.create();
	}

	/**
     * Dialog事件的接口.
     */
    public interface DialogOnClickListener {
    	public void onPositiveClick();
     	public void onNegativeClick();
    }
}
