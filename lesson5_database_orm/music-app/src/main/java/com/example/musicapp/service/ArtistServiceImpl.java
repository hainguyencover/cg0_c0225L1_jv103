package com.example.musicapp.service;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Artist;
import com.example.musicapp.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;


@Service
public class ArtistServiceImpl implements IArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private IFileStorageService fileStorageService;

    @Override
    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    @Override
    public Page<Artist> findAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return artistRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        }
        return artistRepository.findAll(pageable);
    }

    @Override
    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nghệ sĩ với ID: " + id));
    }

    @Override
    public Artist save(Artist artist, MultipartFile avatarFile) {
        if (artist.getId() == null && artistRepository.existsByName(artist.getName())) {
            throw new IllegalArgumentException("Tên nghệ sĩ '" + artist.getName() + "' đã tồn tại.");
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(avatarFile, "images");
            artist.setAvatarPath(fileName);
        }
        return artistRepository.save(artist);
    }

    @Override
    public void delete(Long id) {
        Artist artistToDelete = this.findById(id);
        artistRepository.delete(artistToDelete);
    }
}

