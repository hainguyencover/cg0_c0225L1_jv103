package com.library.librarymanagement.exception;

public class InvalidBorrowCodeException extends Exception {
    public InvalidBorrowCodeException(String message) {
        super(message);
    }

    public InvalidBorrowCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
