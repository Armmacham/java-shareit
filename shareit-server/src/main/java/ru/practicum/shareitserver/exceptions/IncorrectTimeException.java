package ru.practicum.shareitserver.exceptions;

public class IncorrectTimeException extends RuntimeException {
    public IncorrectTimeException(String message) {
        super(message);
    }
}
