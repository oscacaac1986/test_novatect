package com.bank.enums;

public enum TransactionStatus {
    COMPLETED("Completed", "Transaction completed successfully"),
    CANCELLED("Cancelled", "Transaction was cancelled");

    private final String status;
    private final String description;

    TransactionStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
