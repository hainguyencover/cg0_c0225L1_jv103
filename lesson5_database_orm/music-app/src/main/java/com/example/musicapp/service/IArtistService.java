package com.example.musicapp.service;

import com.example.musicapp.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IArtistService {
    List<Artist> findAll();

    Page<Artist> findAll(String keyword, Pageable pageable);

    Artist findById(Long id);

    Artist save(Artist artist, MultipartFile avatarFile);

    void delete(Long id);
}
