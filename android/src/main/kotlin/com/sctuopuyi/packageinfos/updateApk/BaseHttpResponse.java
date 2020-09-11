package com.sctuopuyi.packageinfos.updateApk;

/**
 * Created on 02/03/2017 16:04.
 */

public class BaseHttpResponse<T>{

    public BaseHttpResponse() {
    }

    public BaseHttpResponse(int code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    private int code;
    private String msg;
    private T result;

    @Override
    public String toString() {
        return "BaseHttpResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
