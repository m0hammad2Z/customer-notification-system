package com.digitinarytask.customer.exception;

import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;

public class AccountException extends RuntimeException {
    private final AccountErrorCode errorCode;

    public AccountException(String message, AccountErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AccountErrorCode getErrorCode() {
        return errorCode;
    }
}
