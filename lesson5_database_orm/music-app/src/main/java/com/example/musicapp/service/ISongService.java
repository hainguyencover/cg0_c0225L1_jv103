package com.example.musicapp.service;

import com.example.musicapp.model.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISongService {
    List<Song> findAll();

    Song findById(Long id);

    void save(Song song);

    void update(Song song);

    void delete(Long id);

    String saveFile(MultipartFile file);

    List<Song> search(String keyword);

    List<Song> findPage(int page, int size, String keyword);

    long count(String keyword);
}
