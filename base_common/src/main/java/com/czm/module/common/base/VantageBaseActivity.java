package com.czm.module.common.base;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.czm.module.common.R;
import com.czm.module.common.dialogfragment.DialogUtil;
import com.czm.module.common.utils.StatusBarUtil;
import com.czm.module.common.utils.Utils;
import com.czm.module.common.utils.ViewManager;
import com.orhanobut.logger.Logger;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * <p>Activity基类 </p>
 *
 * @author 2018/5/4
 * @version V1.0.0
 * @name BaseActivity
 */
@Keep
public abstract class VantageBaseActivity extends AppCompatActivity {
    private final static String TAG = BaseActivity.class.getName();
    protected Context mContext;
    private CompositeDisposable compositeDisposable;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("--->onCreate()");
        ViewManager.getInstance().addActivity(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mContext = this;
        initView();
        setEvent();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i("--->onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("--->onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.i("--->onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i("--->onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i("--->onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i("--->onDestroy()");
        ViewManager.getInstance().finishActivity(this);
        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * 初始化activity视图
     */
    protected abstract void initView();

    /**
     * 初始化activity事件
     */
    protected abstract void setEvent();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    protected void addSubscription(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * 添加fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void addFragment(BaseFragment fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .add(frameId, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

    }


    /**
     * 替换fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void replaceFragment(BaseFragment fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .replace(frameId, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commitAllowingStateLoss();

    }


    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    protected void hideFragment(BaseFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 显示fragment
     *
     * @param fragment
     */
    protected void showFragment(BaseFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 移除fragment
     *
     * @param fragment
     */
    protected void removeFragment(BaseFragment fragment) {
        Utils.checkNotNull(fragment);
        getSupportFragmentManager().beginTransaction()
                .remove(fragment)
                .commitAllowingStateLoss();

    }


    /**
     * 弹出栈顶部的Fragment
     */
    protected void popFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    /**
     * 打开个新的activity,不需要判断是否登录
     *
     * @param cls
     */
    public void gotoActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 窗口全屏
     */
    public void setFullScreen(boolean isChange, BaseActivity mActivity) {
        if (!isChange) {
            return;
        }
        mActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 旋转屏幕
     */
    public void setScreenRoate(boolean isChange, BaseActivity mActivity) {
        if (!isChange) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param activity  activity
     * @param view      自定义标题栏
     * @param isPadding 是否头部导航栏间距
     * @param isLight   是否设置浅色状态栏字体图标
     */
    public void transparencyBar(Activity activity, View view, boolean isPadding, boolean isLight) {
        if (StatusBarUtil.getSysType(this) != 0) {
            StatusBarUtil.transparencyBar(activity, view, isPadding);
            if (isLight)
                StatusBarUtil.statusBarLightMode(activity);
            else
                StatusBarUtil.statusBarDarkMode(activity);
        }
    }

    protected void setToolBar(Toolbar toolbar, TextView toolbarTitle, String title) {
        toolbarTitle.setText(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    /**
     * 弹出加载框
     */
    public void showLoading() {
        dialogFragment = DialogUtil.showProgressDialog(mContext, R.anim.progress_circular, getResources().getString(R.string.loading));
    }

    /**
     * 弹出加载框
     *
     * @param s 描述
     */
    public void showLoading(String s) {
        dialogFragment = DialogUtil.showProgressDialog(mContext, R.anim.progress_circular, s);
    }

    /**
     * 隐藏加载框
     */

    public void hideLoading() {
        if (dialogFragment != null) {
            DialogUtil.removeDialog(mContext);
        }
    }
}
