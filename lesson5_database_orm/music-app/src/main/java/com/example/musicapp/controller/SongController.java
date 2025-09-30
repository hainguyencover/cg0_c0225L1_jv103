package com.example.musicapp.controller;

import com.example.musicapp.model.Song;
import com.example.musicapp.service.FileStorageService;
import com.example.musicapp.service.ISongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/songs")
public class SongController {

    private final ISongService songService;

    private final FileStorageService fileStorageService;

    public SongController(ISongService songService, FileStorageService fileStorageService) {
        this.songService = songService;
        this.fileStorageService = fileStorageService;
    }

    @Value("${upload.path}")   // đọc từ application.properties
    private String uploadPath;

    // Hiển thị danh sách bài hát
    @GetMapping
    public String listSongs(Model model) {
        model.addAttribute("songs", songService.findAll());
        return "song/list"; // -> /WEB-INF/views/song/list.jsp (hoặc .html nếu dùng Thymeleaf)
    }

    // Form tạo bài hát mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("song", new Song());
        return "song/create";
    }

    // Xử lý tạo mới (có upload file)
    @PostMapping("/create")
    public String createSong(@ModelAttribute("song") Song song,
                             @RequestParam("file") MultipartFile file) throws IOException {

        // Lưu file
        String fileName = fileStorageService.saveFile(file);
        song.setFilePath("/uploads/" + fileName);

        songService.save(song);
        return "redirect:/songs";
    }

    // Form cập nhật
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Song song = songService.findById(id);
        model.addAttribute("song", song);
        return "song/edit";
    }

    // Xử lý cập nhật
    @PostMapping("/edit")
    public String updateSong(@ModelAttribute("song") Song song) {
        songService.update(song);
        return "redirect:/songs";
    }

    // Xóa bài hát
    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable("id") Long id) {
        songService.delete(id);
        return "redirect:/songs";
    }

    // Nghe nhạc
    @GetMapping("/play/{id}")
    public String playSong(@PathVariable("id") Long id, Model model) {
        Song song = songService.findById(id);
        model.addAttribute("song", song);
        return "song/play";
    }
}
