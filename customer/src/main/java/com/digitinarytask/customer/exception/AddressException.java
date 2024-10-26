package com.digitinarytask.customer.exception;

import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;

public class AddressException extends RuntimeException {

    private final AddressErrorCode errorCode;

    public AddressException(String message, AddressErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AddressErrorCode getErrorCode() {
        return errorCode;
    }
}
