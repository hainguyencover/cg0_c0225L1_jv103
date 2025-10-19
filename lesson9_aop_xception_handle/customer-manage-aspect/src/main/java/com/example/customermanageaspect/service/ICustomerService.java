package com.example.customermanageaspect.service;

import com.example.customermanageaspect.exception.DuplicateEmailException;
import com.example.customermanageaspect.model.Customer;


public interface ICustomerService extends IGeneralService<Customer> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
