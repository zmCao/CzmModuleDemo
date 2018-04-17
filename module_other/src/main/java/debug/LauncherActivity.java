package debug;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.other.OtherLearnActivity;
import com.czm.module.other.R;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            /* If this is not the root activity */
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
//        setContentView(R.layout.activity_loading);
//        StatusBarUtil.transparencyBar(this, null, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtils.showLongToast(R.string.app_name);
        new Handler().postDelayed(() -> {
            gotoActivity(OtherLearnActivity.class);
            finish();
        }, 2000);
    }
}
