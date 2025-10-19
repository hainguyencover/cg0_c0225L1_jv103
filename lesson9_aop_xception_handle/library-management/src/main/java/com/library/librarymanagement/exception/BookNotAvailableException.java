package com.library.librarymanagement.exception;

public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }

    public BookNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
