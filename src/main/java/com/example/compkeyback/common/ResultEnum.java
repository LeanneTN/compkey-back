package com.example.compkeyback.common;

public enum ResultEnum {
    LOCK_IP(0, "ban"),
    NORMAL_IP(1, "normal"),
    ERROR(-1,"wrong");
    private final int code;
    private final String description;

    ResultEnum(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
