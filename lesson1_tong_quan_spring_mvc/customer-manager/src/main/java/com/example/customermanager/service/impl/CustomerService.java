package com.example.customermanager.service.impl;

import com.example.customermanager.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();

    Customer findById(int id);
}
