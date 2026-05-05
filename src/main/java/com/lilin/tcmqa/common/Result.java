package com.lilin.tcmqa.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T>Result<T> success(){
        return new Result(200, "success", null);
    }
    public static <T>Result<T> success(T data){
        return new Result(200, "success", data);
    }
    public static <T>Result<T> fail(String message){
        return new Result(500, message, null);
    }
    public static <T>Result<T> fail(Integer code, String message){
        return new Result(code, message, null);
    }
}
