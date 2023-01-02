package com.heima.common.exception;


import com.heima.common.common.enums.AppHttpCodeEnum;

/**
 * @author peelsannaw
 */
public class CustomException extends RuntimeException {

    private AppHttpCodeEnum appHttpCodeEnum;
    private String msg;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public CustomException(String msg) {
        this.msg = msg;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
