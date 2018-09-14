package com.czm.module.common.dialogfragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.czm.module.common.dialogfragment.AlertDialogFragment.DialogOnClickListener;

public class DialogUtil {

    /**
     * dialog tag
     */
    private static String mDialogTag = "dialog";

    /**
     * 显示一个一般的对话框（图标，标题，string内容，确认，取消）.
     *
     * @param context
     * @param icon
     * @param title           对话框标题内容
     * @param message         对话框提示内容
     * @param onClickListener 点击确认按钮的事件监听
     */
    public static AlertDialogFragment showAlertDialog(Context context, int icon, String title, String message, DialogOnClickListener onClickListener) {
        Activity activity = (Activity) context;
        removeDialog(activity);
        AlertDialogFragment newFragment = AlertDialogFragment.newInstance(icon, title, message, null, onClickListener);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个一般的对话框（图标，标题，string内容，确认，取消）.
     *
     * @param context
     * @param icon
     * @param title           对话框标题内容
     * @param message         对话框提示内容
     * @param DisableBack
     * @param EnableOK
     * @param EnableCancel
     * @param onClickListener 点击确认按钮的事件监听
     */
    public static AlertDialogFragment showAlertDialog(Context context, int icon, String title, String message, boolean DisableBack, boolean EnableOK, boolean EnableCancel, DialogOnClickListener onClickListener) {
        Activity activity = (Activity) context;
        removeDialog(activity);
        AlertDialogFragment newFragment = AlertDialogFragment.newInstance(icon, title, message, null, DisableBack, EnableOK, EnableCancel, onClickListener);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个一般的对话框（图标，标题，string内容，确认，取消）.
     *
     * @param context
     * @param icon
     * @param title           对话框标题内容
     * @param message         对话框提示内容
     * @param DisableBack
     * @param EnableOK
     * @param EnableCancel
     * @param ok              确定按钮文字
     * @param Cancel          取消按钮文字
     * @param onClickListener 点击确认按钮的事件监听
     */
    public static AlertDialogFragment showAlertDialog(Context context, int icon, String title, String message, boolean DisableBack, boolean EnableOK, boolean EnableCancel, String ok, String Cancel, DialogOnClickListener onClickListener) {
        Activity activity = (Activity) context;
        removeDialog(activity);
        AlertDialogFragment newFragment = AlertDialogFragment.newInstance(icon, title, message, null, DisableBack, EnableOK, EnableCancel, ok, Cancel, onClickListener);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 描述：显示加载框.
     *
     * @param context               the context
     * @param indeterminateDrawable
     * @param message               the message
     */
    public static LoadDialogFragment showLoadDialog(Context context, int indeterminateDrawable, String message) {
        Activity activity = (Activity) context;
        removeDialog(activity);
        LoadDialogFragment newFragment = LoadDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setIndeterminateDrawable(indeterminateDrawable);
        newFragment.setMessage(message);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }


    /**
     * 描述：显示进度框.
     * @param context the context
     * @param indeterminateDrawable 用默认请写0
     * @param message the message
     */
//	public static ProgressDialogFragment showProgressDialog(Context context,int indeterminateDrawable,String message) {
//		Activity activity = (Activity)context;
//		removeDialog(activity);
//		ProgressDialogFragment newFragment = ProgressDialogFragment.newInstance(indeterminateDrawable,message);
//		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
//        // 指定一个过渡动画
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		newFragment.show(ft, mDialogTag);
//	    return newFragment;
//    }

    /**
     * 显示进度框
     *
     * @param context
     * @param indeterminateDrawable
     * @param message
     * @return
     */
    public static CustomProgressFragment showProgressDialog(Context context, int indeterminateDrawable, String message) {
        Activity activity = (Activity) context;
        removeDialog(activity);
        CustomProgressFragment newFragment = CustomProgressFragment.newInstance(indeterminateDrawable, message);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 全屏显示一个Fragment
     *
     * @param view
     * @return
     */
    public static SampleDialogFragment showFragment(View view) {

        removeDialog(view);
        Activity activity = (Activity) view.getContext();
        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图  
        ft.add(android.R.id.content, newFragment, mDialogTag).addToBackStack(null).commit();
        return newFragment;
    }

    /**
     * 全屏显示一个Fragment
     *
     * @param view
     * @param DialogTag 窗口标记
     * @return
     */
    public static SampleDialogFragment showFragment(View view, String DialogTag) {
        removeDialog(view);
        Activity activity = (Activity) view.getContext();
        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图
        ft.add(android.R.id.content, newFragment, DialogTag).addToBackStack(null).commit();
        return newFragment;
    }


    /**
     * 显示一个自定义的对话框(有背景层)
     *
     * @param view
     */
    public static SampleDialogFragment showDialog(View view) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个自定义的对话框(有背景层)
     *
     * @param view
     * @param animEnter
     * @param animExit
     * @param animPopEnter
     * @param animPopExit
     * @return
     */
    public static SampleDialogFragment showDialog(View view, int animEnter, int animExit, int animPopEnter, int animPopExit) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setContentView(view);

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个自定义的对话框(有背景层)
     *
     * @param view
     * @param onCancelListener
     * @return
     */
    public static SampleDialogFragment showDialog(View view, DialogInterface.OnCancelListener onCancelListener) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.setOnCancelListener(onCancelListener);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个自定义的对话框(有背景层)
     *
     * @param view
     * @param animEnter
     * @param animExit
     * @param animPopEnter
     * @param animPopExit
     * @param onCancelListener
     * @return
     */
    public static SampleDialogFragment showDialog(View view, int animEnter, int animExit, int animPopEnter, int animPopExit, DialogInterface.OnCancelListener onCancelListener) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.setOnCancelListener(onCancelListener);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个自定义的对话框(无背景层)
     *
     * @param view
     */
    public static SampleDialogFragment showPanel(View view) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Light_Panel);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 显示一个自定义的对话框(无背景层)
     *
     * @param view
     * @param onCancelListener
     * @return
     */
    public static SampleDialogFragment showPanel(View view, DialogInterface.OnCancelListener onCancelListener) {
        Activity activity = (Activity) view.getContext();
        removeDialog(activity);

        // Create and show the dialog.
        SampleDialogFragment newFragment = SampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Light_Panel);
        newFragment.setContentView(view);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画  
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        newFragment.setOnCancelListener(onCancelListener);
        newFragment.show(ft, mDialogTag);
        return newFragment;
    }

    /**
     * 描述：移除Fragment.
     *
     * @param context the context
     */
    public static void removeDialog(Context context) {
        try {
            Activity activity = (Activity) context;
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            // 指定一个过渡动画
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            Fragment prev = activity.getFragmentManager().findFragmentByTag(mDialogTag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            //可能有Activity已经被销毁的异常
            e.printStackTrace();
        }
    }


    /**
     * 描述：移除Fragment和View
     *
     * @param view
     */
    public static void removeDialog(View view) {
        removeDialog(view.getContext());
        removeSelfFromParent(view);
    }

    /**
     * 从父亲布局中移除自己
     *
     * @param v
     */
    public static void removeSelfFromParent(View v) {
        ViewParent parent = v.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(v);
            }
        }
    }

    /**
     * 描述：移除Fragment.
     *
     * @param context the context
     */
    public static void removeDialog(Context context, String DialogTag) {
        try {
            Activity activity = (Activity) context;
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            // 指定一个过渡动画
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            Fragment prev = activity.getFragmentManager().findFragmentByTag(DialogTag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            //可能有Activity已经被销毁的异常
            e.printStackTrace();
        }
    }
}
