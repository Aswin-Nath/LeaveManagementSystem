package com.kira.leavemanagement.exception;

public class UnAuthorizedException extends CustomException {
    public UnAuthorizedException(String message){
        super(message);
    }
}
