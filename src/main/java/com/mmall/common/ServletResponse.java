package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

//在json序列化时，当key为空时，则不添加至json中
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServletResponse<T> implements Serializable {

    private int code;
    private T data;
    private String msg;

    private ServletResponse(int code){
        this.code = code;
    }

    private ServletResponse(int code,T data){
        this.code = code;
        this.data = data;
    }

    private ServletResponse(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    private ServletResponse(int code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    //该注解为：序列化时忽略该方法
    @JsonIgnore
    public boolean isSuccess(){
        return this.code == ResponseCode.SUCCESS.getCode();
    }

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }

    public T getData(){
        return data;
    }

    public static <T> ServletResponse<T> createBySuccess() {
        return new ServletResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServletResponse<T> createBySuccessMessage(String msg) {
        return new ServletResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServletResponse<T> createBySuccess(T data) {
        return new ServletResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServletResponse<T> createBySuccess(String msg, T data) {
        return new ServletResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServletResponse<T> createByError() {
        return new ServletResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getMessage());
    }

    public static <T> ServletResponse<T> createByErrorMessage(String msg) {
        return new ServletResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }

    public static <T> ServletResponse<T> createByError(int code,String msg) {
        return new ServletResponse<T>(code,msg);
    }

}