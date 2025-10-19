package com.library.librarymanagement.entity;

import java.sql.Timestamp;

public class BorrowRecord {
    private Long borrowId;
    private String borrowCode;
    private Integer bookId;
    private String borrowName;
    private Timestamp borrowDate;
    private Timestamp returnDate;
    private String status;
    private String bookTitle;

    public BorrowRecord() {
    }

    public BorrowRecord(String borrowCode, Integer bookId, String borrowName) {
        this.borrowCode = borrowCode;
        this.bookId = bookId;
        this.borrowName = borrowName;
        this.status = "BORROWED";
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public String getBorrowCode() {
        return borrowCode;
    }

    public void setBorrowCode(String borrowCode) {
        this.borrowCode = borrowCode;
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

    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "borrowId=" + borrowId +
                ", borrowCode='" + borrowCode + '\'' +
                ", bookId=" + bookId +
                ", borrowName='" + borrowName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
