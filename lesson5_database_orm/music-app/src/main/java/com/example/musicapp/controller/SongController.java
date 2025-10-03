package com.example.musicapp.controller;

import com.example.musicapp.model.Song;
import com.example.musicapp.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private ISongService songService;
    @Value("${upload.path}")
    private String uploadPath;

    // Hiển thị danh sách bài hát
//    @GetMapping
//    public String listSongs(Model model) {
//        model.addAttribute("songs", songService.findAll());
//        model.addAttribute("isSearch", false);
//        return "song/list"; // -> /WEB-INF/views/song/list.jsp (hoặc .html nếu dùng Thymeleaf)
//    }
    @GetMapping
    public String listSongs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Song> songs = songService.findPage(page, size, keyword);
        long totalItems = songService.count(keyword);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        model.addAttribute("songs", songs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        return "song/list";
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

        if (!file.isEmpty()) {
            String filePath = songService.saveFile(file);
            song.setFilePath(filePath);
        }
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
    @PostMapping("/edit/{id}")
    public String updateSong(
            @PathVariable("id") Long id,
            @ModelAttribute("song") Song song,
            @RequestParam("file") MultipartFile file) {

        Song existing = songService.findById(id);
        if (existing == null) {
            return "redirect:/songs";
        }

        // Cập nhật thông tin cơ bản
        existing.setTitle(song.getTitle());
        existing.setArtist(song.getArtist());
        existing.setGenre(song.getGenre());

        if (!file.isEmpty()) {
            String fileName = songService.saveFile(file);   // trả về newFileName
            existing.setFilePath(fileName);                 // cập nhật vào object đang lưu
        }
        songService.update(existing);
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

    @GetMapping("/search")
    public String searchSongs(@RequestParam("keyword") String keyword, Model model) {
        List<Song> results = songService.search(keyword);
        model.addAttribute("songs", results);
        model.addAttribute("keyword", keyword); // để hiển thị lại trong input

        // đánh dấu là search
        model.addAttribute("isSearch", true);

        return "song/list"; // view list.html
    }
}
