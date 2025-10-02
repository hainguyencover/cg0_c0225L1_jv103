package com.example.blog.repository;

import com.example.blog.model.Blog;

import java.util.List;

public interface IGenerateBlogRepository<T> {
//    List<T> findAll();

    T findById(Long id);

    void save(T t);

    void remove(Long id);

    List<T> findAll(int page, int size, String sortField, boolean asc);

    List<T> searchByTitle(String keyword, int page, int size);

    long count();
}

