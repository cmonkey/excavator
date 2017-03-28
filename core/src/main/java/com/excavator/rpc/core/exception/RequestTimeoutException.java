package com.excavator.rpc.core.exception;

/**
 * Created by cmonkey on 3/29/17.
 */
public class RequestTimeoutException extends  RuntimeException{

    public RequestTimeoutException(){
        super();
    }

    public RequestTimeoutException(String message){
        super(message);
    }
}
