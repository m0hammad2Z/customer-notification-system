package com.digitinarytask.customer.exception;

import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;

public class OrganizationException extends RuntimeException {
    private final OrganizationErrorCode errorCode;

    public OrganizationException(String message, OrganizationErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OrganizationErrorCode getErrorCode() {
        return errorCode;
    }
}
