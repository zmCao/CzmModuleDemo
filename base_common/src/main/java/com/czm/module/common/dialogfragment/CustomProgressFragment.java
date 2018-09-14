package com.czm.module.common.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.czm.module.common.R;

/**
 * Created by czm on 2017/6/14.
 */
public class CustomProgressFragment extends DialogFragment {

    /**
     * Create a new instance of AbProgressDialogFragment.
     */
    public static CustomProgressFragment newInstance(int indeterminateDrawable, String message) {
        CustomProgressFragment f = new CustomProgressFragment();
        Bundle args = new Bundle();
        args.putInt("indeterminateDrawable", indeterminateDrawable);
        args.putString("message", message);
        f.setArguments(args);
        f.setCancelable(false);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.customprogress, container, false);
        ImageView imageView = (ImageView) mContentView.findViewById(R.id.spinnerImageView);
        imageView.setVisibility(View.VISIBLE);
        TextView txt = (TextView) mContentView.findViewById(R.id.message);
        txt.setText(getArguments().getString("message"));
        //加载动画资源
        Animation animation = AnimationUtils.loadAnimation(getActivity(), getArguments().getInt("indeterminateDrawable"));
        //动画完成后，是否保留动画最后的状态，设为true
        animation.setFillAfter(true);
        imageView.startAnimation(animation);

        //去掉黑色的背景
        Dialog mDialog = getDialog();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                CustomProgressFragment.this.dismiss();
                return true;
            }
        });
        return mContentView;
    }
}
