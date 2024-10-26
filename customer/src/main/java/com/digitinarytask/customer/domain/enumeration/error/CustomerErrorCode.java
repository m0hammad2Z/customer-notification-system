package com.digitinarytask.customer.domain.enumeration.error;

public enum CustomerErrorCode {
    CUSTOMER_NOT_FOUND(404, "Customer not found"),
    DUPLICATE_CUSTOMER(409, "Duplicate customer"),
    INVALID_CUSTOMER_TYPE(400, "Invalid customer type"),
    INVALID_CUSTOMER_STATUS(400, "Invalid customer status"),
    CUSTOMER_HAS_ACTIVE_ACCOUNTS(400, "Customer has active accounts"),
    CREATE_CUSTOMER_FAILED(500, "Failed to create customer"),
    INVALID_CUSTOMER_DATA(400, "Invalid customer data"),
    UPDATE_CUSTOMER_FAILED(500, "Failed to update customer"),
    DELETE_CUSTOMER_FAILED(500, "Failed to delete customer");

    private final int code;
    private final String message;

    CustomerErrorCode(int code, String message) {
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
