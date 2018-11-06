package com.arc.face.data;

import com.arc.face.entity.FaceRegist;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 人脸数据库，以文件形式保存本地
 */
public class FaceDB {
    private final String TAG = this.getClass().toString();

    public static String appid = "FQyMt5SVvALqZFinLRdZ6bnhthgvGLrNSv2duGUFgGGe";
    public static String ft_key = "A32YneAtkUM4B3EfC36CGM6HEy5cEQCQxmVqdsvRFpc";
    public static String fd_key = "A32YneAtkUM4B3EfC36CGMDSeEGUZrgDP67PsERcpvQ";
    public static String fr_key = "A32YneAtkUM4B3EfC36CGMi6FGxVAnbBod66KYSTzrz";
    public static String age_key = "A32YneAtkUM4B3EfC36CGMxR3oHSqL4mqgzwXU9XoK1";
    public static String gender_key = "A32YneAtkUM4B3EfC36CGN5aT4Qh4LhHHCqZnZUSxxC";


    private String mDBPath;
    private List<FaceRegist> faceRegistList;
    private AFR_FSDKEngine afr_fsdkEngine;
    private AFR_FSDKVersion afr_fsdkVersion;
    private boolean mUpgrade;

    public FaceDB(String path) {
        mDBPath = path;
        faceRegistList = new ArrayList<>();
        afr_fsdkVersion = new AFR_FSDKVersion();
        afr_fsdkEngine = new AFR_FSDKEngine();
        mUpgrade = false;
        AFR_FSDKError error = afr_fsdkEngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Logger.e("AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            afr_fsdkEngine.AFR_FSDK_GetVersion(afr_fsdkVersion);
            Logger.d(TAG, "AFR_FSDK_GetVersion=" + afr_fsdkVersion.toString());
        }
    }

    /**
     * 注销人脸比对SDK;
     */
    public void destroy() {
        if (afr_fsdkEngine != null) {
            afr_fsdkEngine.AFR_FSDK_UninitialEngine();
        }
    }

    /**
     * 保存信息
     *
     * @return
     */
    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(afr_fsdkVersion.toString() + "," + afr_fsdkVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        if (!faceRegistList.isEmpty()) {
            return false;
        }
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //加载版本信息
            String version_saved = bos.readString();
            if (version_saved.equals(afr_fsdkVersion.toString() + "," + afr_fsdkVersion.getFeatureLevel())) {
                mUpgrade = true;
            }
            //加载所有注册的人名字
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(mDBPath + "/" + name + ".data").exists()) {
                        faceRegistList.add(new FaceRegist(name));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 加载人脸库
     *
     * @return
     */
    public boolean loadFaces() {

        if (loadInfo()) {
            try {
                for (FaceRegist face : faceRegistList) {
                    Logger.d("load name:" + face.mName + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            face.mFaceList.add(afr);
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                    Logger.d("load name: size = " + face.mFaceList.size());
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 添加人脸
     *
     * @param name 名字
     * @param face
     */
    public void addFace(String name, AFR_FSDKFace face) {
        try {
            //检查该名字是否已经注册过了
            boolean add = true;
            for (FaceRegist faceRegist : faceRegistList) {
                if (faceRegist.mName.equals(name)) {
                    faceRegist.mFaceList.add(face);
                    add = false;
                    break;
                }
            }
            //没有注册过
            if (add) {
                FaceRegist faceRegist = new FaceRegist(name);
                faceRegist.mFaceList.add(face);
                faceRegistList.add(faceRegist);
            }

            if (saveInfo()) {
                //更新所有名字,所有名字依次追加写入
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frface : faceRegistList) {
                    bos.writeString(frface.mName);
                }
                bos.close();
                fs.close();

                //保存新的 feature
                fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.close();
                fs.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除人脸
     * @param name 名字
     * @return
     */
    public boolean delete(String name) {
        try {
            //检查该名字是否已经注册过了
            boolean find = false;
            for (FaceRegist frface : faceRegistList) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    faceRegistList.remove(frface);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //更新所有名字,所有名字依次追加写入
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : faceRegistList) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean upgrade() {
        return false;
    }
}
