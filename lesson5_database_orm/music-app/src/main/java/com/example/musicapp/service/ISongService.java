package com.example.musicapp.service;

import com.example.musicapp.model.Song;

import java.util.List;

public interface ISongService {
    List<Song> findAll();

    Song findById(Long id);

    void save(Song song);

    void update(Song song);

    void delete(Long id);
}
