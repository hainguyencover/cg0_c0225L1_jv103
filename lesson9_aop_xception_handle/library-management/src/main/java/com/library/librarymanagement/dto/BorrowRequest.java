package com.library.librarymanagement.dto;

public class BorrowRequest {
    private Integer bookId;
    private String borrowName;

    public BorrowRequest() {
    }

    public BorrowRequest(Integer bookId, String borrowName) {
        this.bookId = bookId;
        this.borrowName = borrowName;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }
}
