package com.example.musicapp.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        // Gửi thông báo lỗi ra view
        model.addAttribute("errorMessage", ex.getMessage());
        // Trả về một trang lỗi chung
        return "error/404";
    }
}
