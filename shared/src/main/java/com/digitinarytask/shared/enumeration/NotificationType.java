package com.digitinarytask.shared.enumeration;

public enum NotificationType {
    CUSTOMER_CREATED("Customer created", "A new customer has been created"),
    CUSTOMER_UPDATED("Customer updated", "Customer information has been updated"),
    CUSTOMER_DELETED("Customer deleted", "Customer has been deleted"),
    ACCOUNT_CREATED("Account created", "A new account has been created"),
    ACCOUNT_UPDATED("Account updated", "Account information has been updated"),
    ACCOUNT_DELETED("Account deleted", "Account has been deleted"),
    ACCOUNT_BALANCE_UPDATED("Account balance updated", "Account balance has been updated");

    private String message;
    private String title;

    NotificationType(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

}
