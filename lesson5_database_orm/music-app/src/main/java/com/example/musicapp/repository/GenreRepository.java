package com.example.musicapp.repository;

import com.example.musicapp.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Page<Genre> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);
}
