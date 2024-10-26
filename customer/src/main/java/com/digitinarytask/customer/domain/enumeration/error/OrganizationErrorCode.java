package com.digitinarytask.customer.domain.enumeration.error;

public enum OrganizationErrorCode {
    ORG_NOT_FOUND(404, "Organization not found"),
    DUPLICATE_REGISTRATION(400, "Organization already registered"),
    INVALID_ORGANIZATION_STATUS(400, "Invalid organization status"),
    ACTIVE_ACCOUNTS_EXIST(400, "Active accounts exist for organization"),
    INVALID_LEGAL_NAME(400, "Invalid legal name"),
    INVALID_ORG_DATA(400, "Invalid organization data"),
    HAS_ACTIVE_ACCOUNTS(400, "Organization has active accounts"),
    INVALID_UPDATE(400, "Invalid organization update"),
    HAS_ACTIVE_CUSTOMERS(400, "Organization has active customers"),
    CREATE_ORG_FAILED(500, "Failed to create organization"),
    UPDATE_ORG_FAILED(500, "Failed to update organization"),
    DELETE_ORG_FAILED(500, "Failed to delete organization"),
    SEARCH_ORG_FAILED(500, "Failed to search organizations"),
    FETCH_ORG_FAILED(500, "Failed to fetch organizations"),
    INVALID_ORG_ID(400, "Invalid organization ID");

    private final int code;
    private final String message;

    OrganizationErrorCode(int code, String message) {
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
