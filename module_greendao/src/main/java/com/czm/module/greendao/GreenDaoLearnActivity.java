package com.czm.module.greendao;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.greendao.db.UserDao;
import com.czm.module.greendao.model.User;

import java.util.List;

import debug.GreenDaoApplication;

@Route(path = "/GreenDao/GreenDaoLearnActivity")
public class GreenDaoLearnActivity extends BaseActivity {

    private long mExitTime;
    private UserDao userDao;
    private TextView txtInfo;
    private int iCount = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greendao_learn);
        userDao = ((GreenDaoApplication) getApplication()).getDaoSession().getUserDao();
        initView();
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

    private void initView() {
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(onClickListener);
        Button btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(onClickListener);
        Button btnQuery = findViewById(R.id.btnQuery);
        btnQuery.setOnClickListener(onClickListener);
        Button btnUdp = findViewById(R.id.btnUdp);
        btnUdp.setOnClickListener(onClickListener);
        txtInfo = findViewById(R.id.txtInfo);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnAdd) {
                user = new User();
                user.setName("曹氏" + iCount);
                user.setAge(20);
                user.setContent("我是");
                long a = userDao.insert(user);
                iCount++;
            } else if (v.getId() == R.id.btnDel) {
                userDao.deleteByKey((long) 0);
            } else if (v.getId() == R.id.btnQuery) {
                List<User> users = userDao.loadAll();
                String username = "";
                for (int i = 0; i < users.size(); i++) {
                    username += users.get(i).getId() + "---" + users.get(i).getName() + "\n";
                }
                txtInfo.setText(username);
            } else if (v.getId() == R.id.btnUdp) {
                User user3 = new User((long) 0, "花千骨", 19, "备注备注");
                userDao.update(user3);
            }
        }
    };
}
