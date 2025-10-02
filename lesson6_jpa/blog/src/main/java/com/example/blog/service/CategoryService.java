package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.model.Category;
import com.example.blog.repository.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public List<Category> findAll(int page, int size, String sortField, boolean asc) {
        return categoryRepository.findAll(page, size, sortField, asc);
    }

    @Override
    public List<Category> searchByTitle(String keyword, int page, int size) {
        return categoryRepository.searchByTitle(keyword, page, size);
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public long countSearch(String keyword) {
        // cách 1: viết riêng query countSearch trong repository (tốt hơn)
        // cách 2: dùng list search rồi .size() (nhưng kém hiệu năng)
        return categoryRepository.searchByTitle(keyword, 0, Integer.MAX_VALUE).size();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll(0, Integer.MAX_VALUE, "name", false);
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void remove(Long id) {
        categoryRepository.remove(id);
    }

}
