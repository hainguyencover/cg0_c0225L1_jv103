package com.example.productmanagementthymeleaf.service;

import com.example.productmanagementthymeleaf.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private static final Map<Long, Product> products = new HashMap<>();

    static {
        products.put(1L, new Product(1L, "iPhone 15", 25000000.0, "Điện thoại Apple", "Apple"));
        products.put(2L, new Product(2L, "Galaxy S24", 20000000.0, "Điện thoại Samsung", "Samsung"));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Product findById(Long id) {
        return products.get(id);
    }

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public void update(Long id, Product product) {
        products.put(id, product);
    }

    @Override
    public void delete(Long id) {
        products.remove(id);
    }

    @Override
    public List<Product> searchByName(String name) {
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
