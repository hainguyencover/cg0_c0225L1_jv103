package com.example.musicapp.controller;

import com.example.musicapp.exception.ResourceNotFoundException;
import com.example.musicapp.model.Genre;
import com.example.musicapp.service.IGenreService;
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
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private IGenreService genreService;

    /**
     * Hiển thị trang quản lý Thể loại với tìm kiếm và phân trang.
     */
    @GetMapping
    public String showGenrePage(@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                @RequestParam(name = "size", required = false, defaultValue = "5") int size,
                                @RequestParam(name = "sort", required = false, defaultValue = "id,desc") String sort,
                                Model model) {

        // Xử lý tham số sort
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Gọi service để lấy dữ liệu đã phân trang và tìm kiếm
        Page<Genre> genrePage = genreService.findAll(keyword, pageable);

        // Đưa dữ liệu ra view
        model.addAttribute("genrePage", genrePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        // Chuẩn bị đối tượng rỗng cho form (nếu không có lỗi từ redirect)
        if (!model.containsAttribute("genre")) {
            model.addAttribute("genre", new Genre());
        }

        return "genre/list";
    }

    /**
     * Xử lý lưu (thêm mới/cập nhật) và xử lý lỗi tên trùng lặp.
     */
    @PostMapping("/save")
    public String saveGenre(@Valid @ModelAttribute("genre") Genre genre,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // Nếu có lỗi validation (ví dụ: tên rỗng)
        if (bindingResult.hasErrors()) {
            // Gửi lại đối tượng genre đã nhập và lỗi về form
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.genre", bindingResult);
            redirectAttributes.addFlashAttribute("genre", genre);
            return "redirect:/genres";
        }

        try {
            genreService.save(genre);
            redirectAttributes.addFlashAttribute("message", "Thao tác thành công!");
        } catch (IllegalArgumentException e) { // Bắt lỗi tên trùng lặp từ Service
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("genre", genre); // Gửi lại để người dùng sửa
        }

        return "redirect:/genres";
    }

    /**
     * Hiển thị form để sửa một Thể loại.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Genre genre = genreService.findById(id);
            redirectAttributes.addFlashAttribute("genre", genre);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/genres";
    }

    /**
     * Xử lý việc xóa một Thể loại.
     */
    @GetMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Đã xóa thể loại thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/genres";
    }
}


