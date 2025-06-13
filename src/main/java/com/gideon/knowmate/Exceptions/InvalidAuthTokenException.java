package com.gideon.knowmate.Exceptions;

public class InvalidAuthTokenException extends RuntimeException{
    public InvalidAuthTokenException(String invalidAccessToken) {
        super(invalidAccessToken);
    }
}
