package com.example.musicapp.service;

import com.example.musicapp.model.Artist;
import com.example.musicapp.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IGenreService {
    List<Genre> findAll();

    Page<Genre> findAll(String keyword, Pageable pageable);

    Genre findById(Long id);

    Genre save(Genre genre);

    void delete(Long id);
}
