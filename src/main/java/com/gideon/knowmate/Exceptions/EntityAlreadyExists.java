package com.gideon.knowmate.Exceptions;

public class EntityAlreadyExists extends RuntimeException{

    public EntityAlreadyExists(String msg){
        super(msg);
    }
}
