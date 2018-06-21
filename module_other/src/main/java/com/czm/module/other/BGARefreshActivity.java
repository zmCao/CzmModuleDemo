package com.czm.module.other;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czm.module.common.base.BaseActivity;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class BGARefreshActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private BGARefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgarefresh);
    }

    private void initRefreshLayout() {
        mRefreshLayout = findViewById(R.id.rl_demo_refresh);
        mRefreshLayout.setDelegate(this);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
