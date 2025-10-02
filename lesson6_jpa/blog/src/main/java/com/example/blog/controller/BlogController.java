package com.example.blog.controller;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.service.IBlogService;
import com.example.blog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/blogs")
public class BlogController {
    @Autowired
    private IBlogService blogService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public String listBlogs(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(defaultValue = "createdAt") String sortField,
                            @RequestParam(defaultValue = "desc") String sortDir,
                            @RequestParam(required = false) String keyword) {

        boolean asc = sortDir.equalsIgnoreCase("asc");

        List<Blog> blogs;
        long totalElements;
        if (keyword != null && !keyword.trim().isEmpty()) {
            blogs = blogService.searchByTitle(keyword, page, size);
            totalElements = blogService.countSearch(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            blogs = blogService.findAll(page, size, sortField, asc);
            totalElements = blogService.count();
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Chuyển LocalDateTime sang String hiển thị, tránh lỗi format
        for (Blog b : blogs) {
            b.setCreatedAtStr(
                    b.getCreatedAt() != null
                            ? b.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "N/A"
            );
        }

        model.addAttribute("blogs", blogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "blog/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("blog", new Blog());
        model.addAttribute("categories", categoryService.findAll());
        return "blog/create";
    }

    @PostMapping("/create")
    public String createBlog(@ModelAttribute Blog blog, @RequestParam Long categoryId, RedirectAttributes redirect) {
        Category category = categoryService.findById(categoryId);
        blog.setCategory(category);
        LocalDateTime now = LocalDateTime.now();
        blog.setCreatedAt(now);
        blog.setCreatedAtStr(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        blogService.save(blog);
        redirect.addFlashAttribute("success", "Blog đã được lưu thành công!");
        return "redirect:/blogs";
    }

    @GetMapping("/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.findById(id);
        if (blog == null) {
            return "error/404";
        }
        blog.setCreatedAtStr(
                blog.getCreatedAt() != null
                        ? blog.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "N/A"
        );
        model.addAttribute("blog", blog);
        return "blog/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Blog blog = blogService.findById(id);
        if (blog == null) {
            return "error/404";
        }
        model.addAttribute("blog", blog);
        model.addAttribute("categories", categoryService.findAll());
        return "blog/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateBlog(@PathVariable Long id, @ModelAttribute Blog blog, @RequestParam Long categoryId, RedirectAttributes redirect) {
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            return "error/404";
        }
        blog.setId(id);
        blog.setCategory(category);
        // giữ nguyên createdAt, không ghi đè
        Blog existingBlog = blogService.findById(id);
        if (existingBlog != null) {
            blog.setCreatedAt(existingBlog.getCreatedAt());
        }
        blogService.save(blog);
        redirect.addFlashAttribute("success", "Blog đã được cập nhật!");
        return "redirect:/blogs";
    }

    @PostMapping("/{id}/delete")
    public String deleteBlog(@PathVariable Long id, RedirectAttributes redirect) {
        Blog blog = blogService.findById(id);
        if (blog == null) {
            return "error/404";
        }
        blogService.remove(id);
        redirect.addFlashAttribute("success", "Blog đã bị xóa!");
        return "redirect:/blogs";
    }
}
