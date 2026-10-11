package com.coderound.backend.exception;

public class SessionNotFoundException extends ResourceNotFoundException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
