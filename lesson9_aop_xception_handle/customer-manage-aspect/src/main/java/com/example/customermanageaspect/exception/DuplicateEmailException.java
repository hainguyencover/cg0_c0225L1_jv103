package com.example.customermanageaspect.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email đã tồn tại trong hệ thống");
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}

