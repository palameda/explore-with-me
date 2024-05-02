package ru.practicum.exception;

public class DenialOfAccessException extends RuntimeException {
    public DenialOfAccessException(String message) {
        super(message);
    }
}
