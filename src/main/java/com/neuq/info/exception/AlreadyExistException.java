package com.neuq.info.exception;

/**
 * Created by xuwenjuan on 18/4/23.
 */
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(){
        super();
    }

    public AlreadyExistException(String message){
        super(message);
    }

    public AlreadyExistException(String message, Throwable cause){
        super(message, cause);
    }
}
