package com.library.librarymanagement.entity;


import java.sql.Timestamp;

public class Book {
    private Integer bookId;
    private String title;
    private String author;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Book() {
    }

    public Book(Integer bookId, String title, String author, String category, Integer totalQuantity, Integer availableQuantity, Timestamp createdAt, Timestamp updatedAt) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", availableQuantity=" + availableQuantity +
                '}';
    }
}
