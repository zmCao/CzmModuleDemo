package com.czm.module.common.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;

import com.czm.module.common.R;

public class ProgressDialogFragment extends DialogFragment {
	
	int mIndeterminateDrawable;
	String mMessage;

	
	/**
	 * Create a new instance of AbProgressDialogFragment.
	 */
	public static ProgressDialogFragment newInstance(int indeterminateDrawable, String message) {
		ProgressDialogFragment f = new ProgressDialogFragment();
		Bundle args = new Bundle();
		args.putInt("indeterminateDrawable", indeterminateDrawable);
		args.putString("message", message);
		f.setArguments(args);
		f.setCancelable(false);
		return f;
	}
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIndeterminateDrawable = getArguments().getInt("indeterminateDrawable");
		mMessage = getArguments().getString("message");
		//ProgressDialog mProgressDialog = new ProgressDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		ProgressDialog mProgressDialog = new ProgressDialog(getActivity(), R.style.CustomProgressDialog);
		if(mIndeterminateDrawable > 0){
			mProgressDialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(mIndeterminateDrawable));
		}
		if(mMessage != null){
			mProgressDialog.setMessage(mMessage);
		}
		
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				ProgressDialogFragment.this.dismiss();
				return true;
			}
		});
	    return mProgressDialog;
	}
}
