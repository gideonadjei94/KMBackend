package com.gideon.knowmate.Exceptions;

public class ExpiredAuthTokenException extends RuntimeException {
    public ExpiredAuthTokenException(String expiredAccessToken) {
        super(expiredAccessToken);
    }
}
