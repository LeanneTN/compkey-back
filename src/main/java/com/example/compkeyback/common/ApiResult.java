package com.example.compkeyback.common;

public class ApiResult<T> {
    private int code;
    private String msg;
    private T data;
    public ApiResult(int code, String description, T data){
        this.code = code;
        this.msg = description;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {//请求成功的判断方法
        return code == 0;
    }
}