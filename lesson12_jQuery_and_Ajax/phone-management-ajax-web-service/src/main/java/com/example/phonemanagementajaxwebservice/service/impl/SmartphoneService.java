package com.example.phonemanagementajaxwebservice.service.impl;

import com.example.phonemanagementajaxwebservice.model.Smartphone;
import com.example.phonemanagementajaxwebservice.repository.ISmartphoneRepository;
import com.example.phonemanagementajaxwebservice.service.ISmartphoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmartphoneService implements ISmartphoneService {

    @Autowired
    private ISmartphoneRepository iSmartphoneRepository;

    @Override
    public Iterable<Smartphone> findAll() {
        return iSmartphoneRepository.findAll();
    }

    @Override
    public Optional<Smartphone> findById(Long id) {
        return iSmartphoneRepository.findById(id);
    }

    @Override
    public Smartphone save(Smartphone smartPhone) {
        return iSmartphoneRepository.save(smartPhone);
    }

    @Override
    public void remove(Long id) {
        iSmartphoneRepository.deleteById(id);
    }

//    @Override
//    public Iterable<Smartphone> findAll(String q, Sort sort) {
//        // Vừa tìm kiếm vừa sắp xếp
//        return iSmartphoneRepository.findAllByProducerContainingOrModelContaining(q, q, sort);
//    }

//    @Override
//    public Iterable<Smartphone> findAll(Sort sort) {
//        // Chỉ sắp xếp
//        return iSmartphoneRepository.findAll(sort);
//    }
}
