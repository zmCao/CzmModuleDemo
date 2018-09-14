package com.czm.module.common.rxjava_retrofit.exception;

import com.czm.module.common.base.Constant;
import com.google.gson.annotations.SerializedName;

public class HttpStatus {
    @SerializedName("status")
    private int mCode;
    @SerializedName("msg")
    private String mMessage;

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    public boolean isCodeInvalid() {
        return mCode != Constant.WEB_RESP_CODE_SUCCESS;
    }
}
