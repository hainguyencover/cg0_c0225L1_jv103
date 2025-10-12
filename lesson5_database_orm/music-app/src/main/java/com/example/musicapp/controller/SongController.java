package com.example.musicapp.controller;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.IArtistService;
import com.example.musicapp.service.IGenreService;
import com.example.musicapp.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private ISongService songService;
    @Autowired
    private IArtistService artistService;
    @Autowired
    private IGenreService genreService;

    /**
     * Phương thức trợ giúp để load danh sách Artist và Genre, tránh lặp code.
     */
    private void loadArtistsAndGenres(Model model) {
        model.addAttribute("artists", artistService.findAll());
        model.addAttribute("genres", genreService.findAll());
    }

    /**
     * Hiển thị danh sách bài hát với chức năng lọc, tìm kiếm và phân trang.
     */
    @GetMapping
    public String listSongs(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Long artistId,
                            @RequestParam(required = false) Long genreId,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {

        // Pageable trong Spring Data JPA là 0-indexed
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        // Gọi service để lấy dữ liệu đã được lọc và phân trang
        Page<Song> songPage = songService.findAllWithFilters(keyword, artistId, genreId, pageable);

        model.addAttribute("songPage", songPage);

        // Gửi lại các giá trị lọc đã chọn để hiển thị trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("artistId", artistId);
        model.addAttribute("genreId", genreId);

        // Gửi danh sách Artist và Genre để đổ vào các dropdown lọc
        loadArtistsAndGenres(model);

        return "song/list";
    }

    /**
     * Hiển thị form tạo bài hát mới.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("song", new Song());
        loadArtistsAndGenres(model); // Load dữ liệu cho các dropdown
        return "song/create";
    }

    /**
     * Hiển thị form cập nhật bài hát.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Song song = songService.findById(id);
            model.addAttribute("song", song);
            loadArtistsAndGenres(model); // Load dữ liệu cho các dropdown
            return "song/edit";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/songs";
        }
    }

    /**
     * Xử lý lưu (thêm mới hoặc cập nhật) một bài hát.
     */
    @PostMapping("/save")
    public String saveSong(@Valid @ModelAttribute("song") Song song,
                           BindingResult bindingResult,
                           @RequestParam("file") MultipartFile file,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            loadArtistsAndGenres(model);
            // Xác định xem nên trả về view create hay edit dựa vào sự tồn tại của ID
            return (song.getId() != null) ? "song/edit" : "song/create";
        }

        // --- Xử lý file một cách an toàn ---
        // Chỉ lưu file mới và cập nhật filePath nếu người dùng có chọn file mới
        if (file != null && !file.isEmpty()) {
            String fileName = songService.saveFile(file);
            song.setFilePath(fileName);
        } else {
            // Nếu là form edit và không có file mới, phải giữ lại file cũ
            if (song.getId() != null) {
                Song existingSong = songService.findById(song.getId());
                song.setFilePath(existingSong.getFilePath());
            }
        }

        songService.save(song);
        redirectAttributes.addFlashAttribute("message", "Lưu bài hát thành công!");
        return "redirect:/songs";
    }

    /**
     * Xóa một bài hát.
     */
    @GetMapping("/delete/{id}")
    public String deleteSong(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            songService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa bài hát thành công!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xóa bài hát.");
        }
        return "redirect:/songs";
    }

    /**
     * Phát nhạc và tăng lượt nghe.
     */
    @GetMapping("/play/{id}")
    public String playSong(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Song song = songService.findById(id);
            // Tăng lượt nghe
            song.setPlayCount(song.getPlayCount() + 1);
            songService.save(song); // Lưu lại thay đổi

            model.addAttribute("song", song);
            return "song/play";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/songs";
        }
    }
}
