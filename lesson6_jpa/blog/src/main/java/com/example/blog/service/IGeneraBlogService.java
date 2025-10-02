package com.example.blog.service;

import java.util.List;

public interface IGeneraBlogService<T> {
    List<T> findAll();

    void save(T t);

    T findById(Long id);

    void remove(Long id);

    List<T> findAll(int page, int size, String sortField, boolean asc); // phân trang + sort

    List<T> searchByTitle(String keyword, int page, int size); // search

    long count();  // tổng số blog

    long countSearch(String keyword); // tổng số blog khi search
}
