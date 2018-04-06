package com.czm.module.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.alibaba.android.arouter.launcher.ARouter;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.BaseFragment;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.common.widget.NoScrollViewPager;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 曹志敏 2018/3/30
 * @version V1.0.0
 * @name BottomNavigationActivity
 */
public class BottomNavigationActivity extends BaseActivity {

    private NoScrollViewPager mPager;
    private List<BaseFragment> mFragments;
    private FragmentAdapter mAdapter;
    private long mExitTime = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.navigation_home) {
                mPager.setCurrentItem(0);
                return true;
            } else if (i == R.id.navigation_device) {
                mPager.setCurrentItem(1);
                return true;
            } else if (i == R.id.navigation_me) {
                mPager.setCurrentItem(2);
                return true;
            } else if (i == R.id.navigation_notifications) {
                mPager.setCurrentItem(3);
                return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottomnavigation);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        disableShiftMode(navigation);
        initViewPager();
    }

    private void initViewPager() {
        mFragments = new ArrayList<>();
        mFragments.add(getHomeFragment());
        mFragments.add(getDeviceFragment());
        mFragments.add(MeFragment.newInstance());
        mFragments.add(MeFragment.newInstance());
        mPager = findViewById(R.id.container_pager);
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragments);
        mPager.setPagerEnabled(false);
        mPager.setAdapter(mAdapter);
    }

    private BaseFragment getHomeFragment() {
        BaseFragment homeFragment = null;
        homeFragment = (BaseFragment) ARouter.getInstance()
                .build("/home/homefragment").navigation();
        Logger.d(homeFragment);
        return homeFragment;
    }

    private BaseFragment getDeviceFragment() {
        BaseFragment deviceFragment = null;
        deviceFragment = (BaseFragment) ARouter.getInstance()
                .build("/customcontrol/customcontrolfragment").navigation();
        Logger.d(deviceFragment);
        return deviceFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //两秒之内按返回键就会退出
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.toastShort(Utils.getContext(), getString(R.string.app_exit_hint));
                mExitTime = System.currentTimeMillis();
            } else {
                ViewManager.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //底部导航栏3个以上 会变样
    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Logger.e("Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Logger.e("Unable to change value of shift mode", e);
        }
    }
}
