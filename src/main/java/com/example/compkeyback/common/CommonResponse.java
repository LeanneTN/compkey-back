package com.example.compkeyback.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
//Springboot Jackson序列化为JSON字符串
@JsonInclude(JsonInclude.Include.NON_NULL)//仅仅序列化非空值
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private T data;

    protected CommonResponse(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //请求成功，无数据返回
    public static <T> CommonResponse<T>  createForSuccess(){
        return new CommonResponse<>(ResultEnum.NORMAL_IP.getCode(),ResultEnum.NORMAL_IP.getDescription(),null);
    }


    //请求成功，并返回指定成功信息
    public static <T> CommonResponse <T> creatForSuccess(String message) {
        return new CommonResponse<T>(ResultEnum.LOCK_IP.getCode(),message,null);
    }

    //请求错误，默认错误信息
    public static <T> CommonResponse<T> createForError(){
        return new CommonResponse<>(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getDescription(), null);
    }

//    @JsonIgnore
//    public boolean isSuccess() {
//        return this.code == ResponseCode.SUCCESS.getCode();
//    }
}
