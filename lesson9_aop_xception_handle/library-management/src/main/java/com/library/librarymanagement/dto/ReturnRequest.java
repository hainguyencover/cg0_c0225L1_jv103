package com.library.librarymanagement.dto;

public class ReturnRequest {
    private String borrowCode;

    public ReturnRequest() {
    }

    public ReturnRequest(String borrowCode) {
        this.borrowCode = borrowCode;
    }

    public String getBorrowCode() {
        return borrowCode;
    }

    public void setBorrowCode(String borrowCode) {
        this.borrowCode = borrowCode;
    }
}
