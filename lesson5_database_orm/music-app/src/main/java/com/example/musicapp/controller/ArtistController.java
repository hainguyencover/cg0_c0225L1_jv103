package com.example.musicapp.controller;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Artist;
import com.example.musicapp.service.IArtistService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private IArtistService artistService;

    @GetMapping
    public String showArtistPage(@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                 @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                 @RequestParam(name = "size", required = false, defaultValue = "5") int size,
                                 @RequestParam(name = "sort", required = false, defaultValue = "id,desc") String sort,
                                 Model model) {

        // Xử lý tham số sắp xếp
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Gọi service để lấy dữ liệu
        Page<Artist> artistPage = artistService.findAll(keyword, pageable);

        // Gửi dữ liệu ra view
        model.addAttribute("artistPage", artistPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort); // Gửi cả tham số sort để view có thể giữ lại

        // Nếu model không chứa "artist" (do redirect từ lỗi), thì tạo mới
        if (!model.containsAttribute("artist")) {
            model.addAttribute("artist", new Artist());
        }

        return "artist/list";
    }

    @PostMapping("/save")
    public String saveArtist(@Valid @ModelAttribute("artist") Artist artist,
                             BindingResult bindingResult,
                             @RequestParam("avatarFile") MultipartFile avatarFile,
                             RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validation (ví dụ: tên rỗng)
        if (bindingResult.hasErrors()) {
            // Gửi lại đối tượng artist chứa lỗi và cả BindingResult về view
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.artist", bindingResult);
            redirectAttributes.addFlashAttribute("artist", artist);
            return "redirect:/artists";
        }

        try {
            artistService.save(artist, avatarFile);
            redirectAttributes.addFlashAttribute("message", "Thao tác thành công!");
        } catch (IllegalArgumentException e) { // Bắt lỗi tên trùng lặp từ Service
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("artist", artist); // Gửi lại artist để điền form
        }

        return "redirect:/artists";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Artist artist = artistService.findById(id);
            redirectAttributes.addFlashAttribute("artist", artist);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/artists";
    }

    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            artistService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa nghệ sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/artists";
    }
}

