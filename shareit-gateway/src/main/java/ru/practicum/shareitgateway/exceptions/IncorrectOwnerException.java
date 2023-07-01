package ru.practicum.shareitgateway.exceptions;

 import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IncorrectOwnerException extends RuntimeException {
    public IncorrectOwnerException(String message) {
        super(message);
    }
}