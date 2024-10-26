package com.digitinarytask.customer.exception;

import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;

public class CustomerException extends RuntimeException {

    private final CustomerErrorCode errorCode;

    public CustomerException(String message, CustomerErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomerErrorCode getErrorCode() {
        return errorCode;
    }
}
