package com.czm.module.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.BaseFragment;
import com.czm.module.common.utils.Utils;

@Route(path = "/main/mefragment")
public class MeFragment extends BaseFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MeFragment.
     */
    public static MeFragment newInstance() {
        return new MeFragment();
    }


    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        Button btnDemo = view.findViewById(R.id.btnDemo);
        btnDemo.setOnClickListener(v -> getHoldingActivity().gotoActivity(MainActivity.class));
        return view;
    }
}
