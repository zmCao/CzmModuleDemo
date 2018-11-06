package com.arc.face.entity;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.ArrayList;
import java.util.List;

public class FaceRegist {
    public String mName;
    public List<AFR_FSDKFace> mFaceList;

    public FaceRegist(String name) {
        mName = name;
        mFaceList = new ArrayList<>();
    }
}
