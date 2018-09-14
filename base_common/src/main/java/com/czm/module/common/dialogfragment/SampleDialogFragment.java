package com.czm.module.common.dialogfragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SampleDialogFragment extends DialogFragment {

    private View mContentView;
	private DialogInterface.OnCancelListener mOnCancelListener = null;
	private DialogInterface.OnDismissListener mOnDismissListener = null;

	/**
	 * Create a new instance of AbDialogFragment, providing "style" as an
	 * argument.
	 */
	public static SampleDialogFragment newInstance(int style, int theme) {
		SampleDialogFragment f = new SampleDialogFragment();

		// Supply style input as an argument.
		Bundle args = new Bundle();
		args.putInt("style", style);
		args.putInt("theme", theme);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
        int mStyle = getArguments().getInt("style");
        int mTheme = getArguments().getInt("theme");
		setStyle(mStyle, mTheme);
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		// 用户中断
		if(mOnCancelListener != null){
			mOnCancelListener.onCancel(dialog);
		}
		
		super.onCancel(dialog);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// 用户隐藏
		if(mOnDismissListener != null){
		    mOnDismissListener.onDismiss(dialog);
		}
		super.onDismiss(dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		return mContentView;
	}

	public View getContentView() {
		return mContentView;
	}

	public void setContentView(View mContentView) {
		this.mContentView = mContentView;
	}

	public DialogInterface.OnCancelListener getOnCancelListener() {
		return mOnCancelListener;
	}

	public void setOnCancelListener(
			DialogInterface.OnCancelListener onCancelListener) {
		this.mOnCancelListener = onCancelListener;
	}

	public DialogInterface.OnDismissListener getOnDismissListener() {
		return mOnDismissListener;
	}

	public void setOnDismissListener(
			DialogInterface.OnDismissListener onDismissListener) {
		this.mOnDismissListener = onDismissListener;
	}
	
}
