package com.example.springuploadmusic.service;

import com.example.springuploadmusic.model.Song;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SongService {

    // Giả lập cơ sở dữ liệu bằng một List
    private final List<Song> songDatabase = new ArrayList<>();

    // Thư mục để lưu trữ các file nhạc upload lên.
    // LƯU Ý: Bạn cần thay đổi đường dẫn này thành một đường dẫn tồn tại trên máy của bạn.
    private final Path rootLocation = Paths.get("music-uploads");

    public SongService() {
        try {
            // Tạo thư mục nếu nó chưa tồn tại
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * Lưu thông tin bài hát và file nhạc
     * @param song      Thông tin bài hát
     * @param file      File nhạc được upload
     * @throws IOException
     */
    public void save(Song song, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Tạo một tên file duy nhất để tránh trùng lặp
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Lưu file vào thư mục đã định nghĩa
        Files.copy(file.getInputStream(), this.rootLocation.resolve(uniqueFileName));

        // Cập nhật đường dẫn file và lưu thông tin vào "database"
        song.setFilePath(this.rootLocation.resolve(uniqueFileName).toString());
        songDatabase.add(song);
    }

    /**
     * Lấy danh sách tất cả bài hát
     * @return List<Song>
     */
    public List<Song> findAll() {
        // Trả về một bản sao của danh sách để tránh bị sửa đổi từ bên ngoài
        return Collections.unmodifiableList(songDatabase);
    }
}
