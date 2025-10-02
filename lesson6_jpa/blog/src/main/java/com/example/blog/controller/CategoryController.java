package com.example.blog.controller;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<Category> categories;
        long total;

        if (keyword != null && !keyword.isEmpty()) {
            categories = categoryService.searchByTitle(keyword, page, size);
            total = categoryService.countSearch(keyword);
        } else {
            categories = categoryService.findAll(page, size, "id", true);
            total = categoryService.count();
        }

        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        return "category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirect) {
        categoryService.save(category);
        redirect.addFlashAttribute("success", "Category đã được tạo thành công!");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "category/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirect) {
        category.setId(id);
        categoryService.save(category);
        redirect.addFlashAttribute("success", "Category đã được cập nhật!");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirect) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return "error/404";
        }
        categoryService.remove(id);
        redirect.addFlashAttribute("success", "Category đã được xóa!");
        return "redirect:/categories";
    }
}
