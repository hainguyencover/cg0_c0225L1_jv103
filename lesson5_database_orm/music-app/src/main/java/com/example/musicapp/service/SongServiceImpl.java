package com.example.musicapp.service;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Song;
import com.example.musicapp.repository.SongRepository;
import com.example.musicapp.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl implements ISongService {

    // Đường dẫn lưu file được đọc từ application.properties
    @Value("${upload.path}")
    private String uploadPath;

    // Tự động inject SongRepository để làm việc với DB
    @Autowired
    private SongRepository songRepository;

    /**
     * Phương thức tìm kiếm và phân trang toàn diện.
     * Gọi đến phương thức query phức tạp trong SongRepository.
     */
    @Override
    public Page<Song> findAllWithFilters(String keyword, Long artistId, Long genreId, Pageable pageable) {
        return songRepository.findWithFilters(keyword, artistId, genreId, pageable);
    }

    /**
     * Lấy tất cả bài hát, không phân trang.
     */
    @Override
    public List<Song> findAll() {
        return songRepository.findAll();
    }

    /**
     * Tìm bài hát theo ID.
     * Sử dụng .orElseThrow() để trả về Song nếu tồn tại, hoặc ném Exception nếu không.
     */
    @Override
    public Song findById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài hát với ID: " + id));
    }

    /**
     * Lưu một bài hát.
     * JpaRepository.save() đủ thông minh để xử lý cả thêm mới (nếu id=null) và cập nhật (nếu id!=null).
     */
    @Override
    public Song save(Song song) {
        return songRepository.save(song);
    }

    /**
     * Xóa một bài hát.
     * Tái sử dụng findById để đảm bảo bài hát tồn tại trước khi xóa.
     */
    @Override
    public void delete(Long id) {
        Song songToDelete = this.findById(id); // Nếu không tìm thấy, dòng này sẽ ném Exception
        songRepository.delete(songToDelete);
    }

    /**
     * Xử lý lưu file nhạc được tải lên.
     */
    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Chuẩn hóa tên file để tránh các lỗi bảo mật (path traversal)
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Kiểm tra các ký tự không hợp lệ
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Tên file chứa ký tự không hợp lệ: " + originalFileName);
            }

            // Tạo tên file duy nhất bằng UUID để tránh trùng lặp
            String fileExtension = StringUtils.getFilenameExtension(originalFileName);
            String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // Tạo đường dẫn đích
            Path targetLocation = Paths.get(uploadPath).resolve(newFileName);

            // Copy file vào thư mục đích. REPLACE_EXISTING nghĩa là ghi đè nếu file đã tồn tại.
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + originalFileName + ". Vui lòng thử lại!", ex);
        }
    }
}


