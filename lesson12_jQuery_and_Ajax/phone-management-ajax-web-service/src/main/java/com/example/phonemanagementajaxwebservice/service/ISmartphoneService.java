package com.example.phonemanagementajaxwebservice.service;

import com.example.phonemanagementajaxwebservice.model.Smartphone;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface ISmartphoneService {
    Iterable<Smartphone> findAll();

    Optional<Smartphone> findById(Long id);

    Smartphone save(Smartphone smartPhone);

    void remove(Long id);

//    Iterable<Smartphone> findAll(String q, Sort sort);
//
//    Iterable<Smartphone> findAll(Sort sort);
}
