package debug

import android.content.Intent
import android.os.Bundle
import android.os.Handler

import com.czm.module.common.base.BaseActivity
import com.czm.module.common.utils.ToastUtils
import com.wkt.module_kotlin.MainActivity

class LoadingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            /* If this is not the root activity */
            val intent = intent
            val action = intent.action
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
                finish()
                return
            }
        }
        //        setContentView(R.layout.activity_loading);
        //        StatusBarUtil.transparencyBar(this, null, false);
    }

    override fun onResume() {
        super.onResume()
        ToastUtils.showLongToast("组件模式运行")
        Handler().postDelayed({gotoActivity(MainActivity().javaClass)},2000)
        //        new Handler().postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //
        //            }
        //        }, 2000);
    }
}
