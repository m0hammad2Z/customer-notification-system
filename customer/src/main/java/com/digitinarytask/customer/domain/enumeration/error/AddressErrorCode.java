package com.digitinarytask.customer.domain.enumeration.error;

/**
 * Represents the address error codes.
 */
public enum AddressErrorCode {
    ADDRESS_NOT_FOUND(404, "Address not found"),
    ADDRESS_ALREADY_EXISTS(409, "Address already exists"),
    ADDRESS_INVALID(400, "Invalid address"),
    ADDRESS_INVALID_TYPE(400, "Invalid address type"),
    LAST_ADDRESS_DELETION_ERROR(400, "Last address cannot be deleted"),
    ADDRESS_DELETION_ERROR(400, "Address deletion error"),
    ADDRESS_CREATION_ERROR(400, "Address creation error"),
    ADDRESS_UPDATE_ERROR(400, "Address update error"),
    ADDRESS_RETRIEVAL_ERROR(400, "Address retrieval error");

    private final int code;
    private final String message;

    AddressErrorCode(int code, String message) {
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
