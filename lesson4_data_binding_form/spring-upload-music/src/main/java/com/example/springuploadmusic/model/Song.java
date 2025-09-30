package com.example.springuploadmusic.model;

public class Song {
    private String name;
    private String artist;
    private String genre; // Thể loại nhạc, có thể nhập nhiều thể loại cách nhau bằng dấu phẩy
    private String filePath; // Đường dẫn lưu file trên server

    public Song() {
    }

    public Song(String name, String artist, String genre) {
        this.name = name;
        this.artist = artist;
        this.genre = genre;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
