package com.example.springgreeting.service;

import com.example.springgreeting.model.Student;

import java.util.List;

public interface IStudentService {
    List<Student> findAll(String q, String sort, String dir, int page, int size);

    Student findById(String id);

    void create(Student s);

    void update(Student s);

    void delete(String id);

    boolean existsById(String id);

    long count(String q);
}
