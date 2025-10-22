package com.example.shoppingcart.service;

import com.example.shoppingcart.model.Product;

import java.util.Optional;

public interface IProductService {
    Iterable<Product> findAll();

    Optional<Product> findById(Long id);
}
