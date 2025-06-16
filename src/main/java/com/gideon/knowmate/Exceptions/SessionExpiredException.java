package com.gideon.knowmate.Exceptions;

public class SessionExpiredException extends RuntimeException{

    public SessionExpiredException(String msg){
        super(msg);
    }
}
