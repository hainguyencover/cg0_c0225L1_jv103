package com.example.customermanagement.service;

import com.example.customermanagement.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();

    Customer findById(Long id);

    void save(Customer customer);
}
