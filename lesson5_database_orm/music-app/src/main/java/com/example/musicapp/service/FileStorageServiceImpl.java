package com.example.musicapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    private final Path musicStorageLocation;
    private final Path imageStorageLocation;

    // Constructor để inject đường dẫn từ application.properties
    public FileStorageServiceImpl(@Value("${upload.path.music}") String musicPath,
                                  @Value("${upload.path.images}") String imagePath) {
        this.musicStorageLocation = Paths.get(musicPath).toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get(imagePath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.musicStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục để lưu trữ file.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Lấy tên file gốc
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Kiểm tra các ký tự không hợp lệ
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Tên file chứa ký tự không hợp lệ " + originalFileName);
            }

            // Tạo tên file duy nhất
            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                // Ignore
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Chọn thư mục lưu trữ
            Path targetLocation;
            if ("images".equalsIgnoreCase(subDirectory)) {
                targetLocation = this.imageStorageLocation.resolve(newFileName);
            } else {
                targetLocation = this.musicStorageLocation.resolve(newFileName);
            }

            // Copy file vào thư mục đích
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file " + originalFileName + ". Vui lòng thử lại!", ex);
        }
    }
}

