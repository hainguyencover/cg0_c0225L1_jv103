package com.example.blog.service;

import java.util.List;

public interface IGeneraBlogService<T> {
    List<T> findAll();

    void save(T t);

    T findById(Long id);

    void remove(Long id);
}
