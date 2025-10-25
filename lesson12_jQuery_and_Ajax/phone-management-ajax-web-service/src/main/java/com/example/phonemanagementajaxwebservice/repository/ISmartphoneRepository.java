package com.example.phonemanagementajaxwebservice.repository;

import com.example.phonemanagementajaxwebservice.model.Smartphone;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISmartphoneRepository extends JpaRepository<Smartphone, Long> {
    // Chỉ một phương thức này là đủ cho cả tìm kiếm và sắp xếp
    Iterable<Smartphone> findAllByProducerContainingIgnoreCaseOrModelContainingIgnoreCase(String producer, String model, Sort sort);
}
