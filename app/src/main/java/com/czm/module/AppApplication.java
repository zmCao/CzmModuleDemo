package com.czm.module;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.czm.module.common.base.BaseApplication;
import com.czm.module.common.utils.Utils;
/**
 * <p>这里仅需做一些初始化的工作</p>
 *
 * @author 曹志敏 2018/3/30
 * @version V1.0.0
 * @name AppApplication
 */
public class AppApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        if (Utils.isAppDebug()) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // dex突破65535的限制
        MultiDex.install(this);
    }
}
