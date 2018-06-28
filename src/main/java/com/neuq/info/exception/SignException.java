package com.neuq.info.exception;

/**
 * @author Lin Dexiang
 * @date 2018/6/28
 */

public class SignException extends RuntimeException{
    public SignException() {
        super();
    }

    public SignException(String message){
        super(message);
    }

    public SignException(String message, Throwable cause){
        super(message, cause);
    }

}
