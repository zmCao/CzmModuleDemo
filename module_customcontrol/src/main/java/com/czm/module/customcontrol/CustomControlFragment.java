package com.czm.module.customcontrol;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseFragment;

@Route(path = "/customcontrol/customcontrolfragment")
public class CustomControlFragment extends BaseFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static CustomControlFragment newInstance() {
        return new CustomControlFragment();
    }


    public CustomControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customcontrol, container, false);
        Button btnDemo = view.findViewById(R.id.btnDemo);
        btnDemo.setOnClickListener(v -> getHoldingActivity().gotoActivity(CustomControlLearnActivity.class));
        return view;
    }


}
