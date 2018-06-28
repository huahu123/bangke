package com.neuq.info.exception;

/**
 * @author Lin Dexiang
 * @date 2018/4/24
 */

public class ArgsErrorException extends AlreadyExistException {

    public ArgsErrorException() {
        super();
    }

    public ArgsErrorException(String message){
        super(message);
    }

    public ArgsErrorException(String message, Throwable cause){
        super(message, cause);
    }

}
