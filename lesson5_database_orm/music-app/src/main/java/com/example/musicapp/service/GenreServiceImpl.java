package com.example.musicapp.service;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Artist;
import com.example.musicapp.model.Genre;
import com.example.musicapp.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements IGenreService {
    @Autowired
    private GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Page<Genre> findAll(String keyword, Pageable pageable) {
        // Nếu có từ khóa, thực hiện tìm kiếm theo tên (không phân biệt hoa thường).
        // Ngược lại, lấy tất cả các thể loại.
        if (keyword != null && !keyword.trim().isEmpty()) {
            return genreRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        }
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre findById(Long id) {
        // Sử dụng .orElseThrow để trả về Genre nếu tìm thấy,
        // hoặc ném ra exception nếu không tìm thấy.
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
    }

    @Override
    public Genre save(Genre genre) {
        // Logic nghiệp vụ quan trọng: Kiểm tra tên trùng lặp.
        // Điều kiện `genre.getId() == null` đảm bảo việc kiểm tra chỉ xảy ra khi TẠO MỚI.
        if (genre.getId() == null && genreRepository.existsByName(genre.getName())) {
            // Ném ra một exception có ý nghĩa rõ ràng để Controller có thể bắt và xử lý.
            throw new IllegalArgumentException("Tên thể loại '" + genre.getName() + "' đã tồn tại.");
        }
        return genreRepository.save(genre);
    }

    @Override
    public void delete(Long id) {
        // Tái sử dụng findById để kiểm tra sự tồn tại trước khi thực hiện xóa.
        // Nếu không tìm thấy, findById sẽ tự động ném exception và dừng lại.
        Genre genreToDelete = this.findById(id);
        genreRepository.delete(genreToDelete);
    }
}
