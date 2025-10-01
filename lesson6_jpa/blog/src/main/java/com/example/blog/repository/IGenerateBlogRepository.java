package com.example.blog.repository;

import java.util.List;

public interface IGenerateBlogRepository<T> {
    List<T> findAll();

    T findById(Long id);

    void save(T t);

    void remove(Long id);
}
