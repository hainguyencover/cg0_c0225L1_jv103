package com.example.productmanagementthymeleaf.service;

import com.example.productmanagementthymeleaf.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> findAll();
    Product findById(Long id);
    void save(Product product);
    void update(Long id, Product product);
    void delete(Long id);
    List<Product> searchByName(String name);
}
