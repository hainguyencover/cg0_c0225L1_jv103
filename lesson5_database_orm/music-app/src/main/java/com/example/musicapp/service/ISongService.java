package com.example.musicapp.service;

import com.example.musicapp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISongService {

    Page<Song> findAllWithFilters(String keyword, Long artistId, Long genreId, Pageable pageable);


    List<Song> findAll();


    Song findById(Long id);


    Song save(Song song);


    void delete(Long id);


    String saveFile(MultipartFile file);
}
