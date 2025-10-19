package com.example.customermanageaspect.service.impl;

import com.example.customermanageaspect.exception.DuplicateEmailException;
import com.example.customermanageaspect.model.Customer;
import com.example.customermanageaspect.repository.ICustomerRepository;
import com.example.customermanageaspect.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerService implements ICustomerService {

    @Autowired
    private ICustomerRepository customerRepository;

    @Override
    public Iterable<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer save(Customer customer) throws DuplicateEmailException {
        // Kiểm tra email trùng khi tạo mới
        if (customer.getId() == null && existsByEmail(customer.getEmail())) {
            throw new DuplicateEmailException("Email '" + customer.getEmail() + "' đã tồn tại");
        }

        // Kiểm tra email trùng khi cập nhật
        if (customer.getId() != null && existsByEmailAndIdNot(customer.getEmail(), customer.getId())) {
            throw new DuplicateEmailException("Email '" + customer.getEmail() + "' đã tồn tại");
        }

        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void remove(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }
}
