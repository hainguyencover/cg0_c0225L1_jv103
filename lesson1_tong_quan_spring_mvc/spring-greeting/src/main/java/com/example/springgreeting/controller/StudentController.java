package com.example.springgreeting.controller;

import com.example.springgreeting.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {
    private final List<Student> students = new ArrayList<>();

    StudentController() {
        students.add(new Student("SV001", "Nguyen Van A", 8.5f));
        students.add(new Student("SV002", "Tran Thi B", 7.2f));
        students.add(new Student("SV003", "Nguyen Van C", 8.5f));
        students.add(new Student("SV004", "Tran Thi D", 7.2f));
        students.add(new Student("SV005", "Nguyen Van E", 8.5f));
    }

    @GetMapping
    private String listStudents(Model model) {
        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/add")
    private String addStudent(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/add")
    private String addStudent(@ModelAttribute Student student) {
        students.add(student);
        return "redirect:/students";
    }
}
