package com.czm.module.common.rxjava_retrofit.exception;

import com.czm.module.common.base.Constant;

public class ApiException extends RuntimeException {
    private int mErrorCode;

    public ApiException(int errorCode, String errorMessage) {
        super(errorMessage);
        mErrorCode = errorCode;
    }

    /**
     * 判断是否是token失效
     *
     * @return 失效返回true, 否则返回false;
     */
    public boolean isTokenExpried() {
        return mErrorCode == Constant.TOKEN_EXPRIED;
    }
}
