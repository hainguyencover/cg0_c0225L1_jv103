package com.example.nasa.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "feedback")
public class FeedBack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;

    @Column(length = 1000)
    private String comment;

    private int rating; // 1-5 stars

    private int likes = 0;

    private LocalDate date; // ngày feedback

    public FeedBack() {
        this.date = LocalDate.now();
    }

    public FeedBack(Long id, String author, String comment, int rating, int likes, LocalDate date) {
        this.id = id;
        this.author = author;
        this.comment = comment;
        this.rating = rating;
        this.likes = likes;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    public int getLikes() {
        return likes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
