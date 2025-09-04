package com.ecom.Security_Service.exception;

public class AccountLockedException extends RuntimeException{

    public AccountLockedException(String message) {
        super(message);
    }
}
