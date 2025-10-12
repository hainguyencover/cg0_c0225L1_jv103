package com.example.musicapp.repository;

import com.example.musicapp.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    /**
     * PHƯƠNG THỨC LỌC NÂNG CAO
     * - keyword: Tìm kiếm trong tên bài hát.
     * - artistId: Lọc theo ID của nghệ sĩ. Nếu null, bỏ qua điều kiện này.
     * - genreId: Lọc theo ID của thể loại. Nếu null, bỏ qua điều kiện này.
     */
    @Query("SELECT DISTINCT s FROM Song s " +
            "JOIN s.artist a " +
            "LEFT JOIN s.genres g " +
            "WHERE " +
            "(:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:artistId IS NULL OR a.id = :artistId) AND " +
            "(:genreId IS NULL OR g.id = :genreId)")
    Page<Song> findWithFilters(@Param("keyword") String keyword,
                               @Param("artistId") Long artistId,
                               @Param("genreId") Long genreId,
                               Pageable pageable);

    // Sau này, chúng ta sẽ thêm các phương thức tìm kiếm nâng cao vào đây, ví dụ:
    // Page<Song> findByTitleContaining(String title, Pageable pageable);
    // Page<Song> findByArtist(Artist artist, Pageable pageable);
    @Query("SELECT s FROM Song s JOIN s.artist a LEFT JOIN s.genres g WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Song> searchEverything(@Param("keyword") String keyword, Pageable pageable);

    // Một phiên bản đơn giản hơn nếu chỉ muốn tìm theo tên bài hát
    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

