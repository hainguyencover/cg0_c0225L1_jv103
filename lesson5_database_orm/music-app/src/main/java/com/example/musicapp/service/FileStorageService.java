package com.example.musicapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    @Value("${upload.path}")
    private String uploadPath;

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File trống!");
        }
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Tạo file đích (ghi rõ đường dẫn tuyệt đối)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dest = Paths.get(uploadPath, fileName); // dùng Paths.get thay vì cộng chuỗi
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return fileName; // chỉ trả tên file, để lưu vào DB
    }
}
