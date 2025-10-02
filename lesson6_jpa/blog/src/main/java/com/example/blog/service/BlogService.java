package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.repository.IBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService implements IBlogService {
    @Autowired
    private IBlogRepository blogRepository;

    @Override
    public List<Blog> findAll() {
        return blogRepository.findAll(0, Integer.MAX_VALUE, "createdAt", false);
    }

    @Override
    public List<Blog> findAll(int page, int size, String sortField, boolean asc) {
        return blogRepository.findAll(page, size, sortField, asc);
    }

    @Override
    public List<Blog> searchByTitle(String keyword, int page, int size) {
        return blogRepository.searchByTitle(keyword, page, size);
    }

    @Override
    public void save(Blog blog) {
        blogRepository.save(blog);
    }

    @Override
    public Blog findById(Long id) {
        return blogRepository.findById(id);
    }

    @Override
    public void remove(Long id) {
        blogRepository.remove(id);
    }

    @Override
    public long count() {
        return blogRepository.count();
    }

    @Override
    public long countSearch(String keyword) {
        // cách 1: viết riêng query countSearch trong repository (tốt hơn)
        // cách 2: dùng list search rồi .size() (nhưng kém hiệu năng)
        return blogRepository.searchByTitle(keyword, 0, Integer.MAX_VALUE).size();
    }
}
