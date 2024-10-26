package com.digitinarytask.customer.domain.enumeration.error;

public enum AccountErrorCode {
    ACCOUNT_NOT_FOUND(404, "Account not found"),
    INSUFFICIENT_BALANCE(400, "Insufficient balance"),
    INVALID_ACCOUNT_STATUS(400, "Invalid account status"),
    DUPLICATE_ACCOUNT_NUMBER(400, "Duplicate account number"),
    CUSTOMER_NOT_FOUND(404, "Customer not found"),
    INVALID_ACCOUNT_UPDATE(400, "Invalid account update"),
    TRANSACTION_LIMIT_EXCEEDED(400, "Transaction limit exceeded"),
    INVALID_ACCOUNT_NUMBER(400, "Invalid account number"),
    INVALID_ACCOUNT_DATA(400, "Invalid account data"),
    INVALID_STATUS_TRANSITION(400, "Invalid status transition"),
    ACCOUNT_DELETION_ERROR(500, "Account deletion error"),
    ACCOUNT_UPDATE_ERROR(500, "Account update error"),
    ACCOUNT_CREATION_ERROR(500,"Account creation error"),
    INVALID_ACCOUNT_ID(400, "Invalid account ID");

    private final int code;
    private final String message;

    AccountErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
