package com.example.springuploadmusic.controller;

import com.example.springuploadmusic.model.Song;
import com.example.springuploadmusic.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class SongController {

    private final SongService songService;
    private final List<String> allowedExtensions = Arrays.asList(".mp3", ".wav", ".ogg", ".m4p");

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/songs")
    public String showSongList(Model model) {
        model.addAttribute("songs", songService.findAll());
        return "song-list";
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        // Thêm một đối tượng Song trống để form binding
        model.addAttribute("song", new Song());
        return "upload-form";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@ModelAttribute Song song,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra file có trống không
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn một file để upload.");
            return "redirect:/upload";
        }

        // 2. Kiểm tra đuôi file có hợp lệ không
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isValidExtension(fileName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Định dạng file không hợp lệ. Chỉ chấp nhận .mp3, .wav, .ogg, .m4p.");
            return "redirect:/upload";
        }

        // 3. Xử lý upload
        try {
            songService.save(song, file);
            redirectAttributes.addFlashAttribute("successMessage", "Upload bài hát '" + fileName + "' thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Upload file thất bại. Vui lòng thử lại.");
            return "redirect:/upload";
        }

        return "redirect:/songs";
    }

    private boolean isValidExtension(String fileName) {
        return allowedExtensions.stream().anyMatch(fileName::endsWith);
    }
}
