package com.example.customermanageaspect.service;

import com.example.customermanageaspect.exception.DuplicateEmailException;
import com.example.customermanageaspect.model.Customer;

import java.util.Optional;

public interface IGeneralService<T> {
    Iterable<T> findAll();

    T save(T t);

    Optional<T> findById(Long id);

    void remove(Long id);
}
