package com.example.musicapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên nghệ sĩ không được để trống")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "avatar_path", nullable = true)
    private String avatarPath;

    // Một nghệ sĩ có nhiều bài hát.
    // 'mappedBy = "artist"' chỉ ra rằng mối quan hệ này được quản lý bởi trường 'artist' trong class Song.
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Song> songs;

    // Constructors
    public Artist() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
