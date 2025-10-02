package com.example.blog.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private String createdAtStr;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Blog() {
    }

    public Blog(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = LocalDateTime.now(); // tự động gán ngày hiện tại
        this.createdAtStr = this.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // Constructor đầy đủ (ví dụ khi load từ DB hoặc test)
    public Blog(Long id, String title, String content, LocalDateTime createdAt, Category category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.category = category;
        this.createdAtStr = (createdAt != null)
                ? createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;
    }

    // getter / setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCreatedAtStr() {
        return createdAtStr;
    }

    public void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }
}
