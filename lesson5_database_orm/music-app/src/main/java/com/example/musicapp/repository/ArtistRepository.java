package com.example.musicapp.repository;

import com.example.musicapp.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức như:
    // - save(Artist artist)
    // - findById(Long id)
    // - findAll()
    // - deleteById(Long id)
    // ... và nhiều hơn nữa!
    // Chúng ta có thể thêm các phương thức truy vấn tùy chỉnh ở đây sau này.
    Page<Artist> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);
}
