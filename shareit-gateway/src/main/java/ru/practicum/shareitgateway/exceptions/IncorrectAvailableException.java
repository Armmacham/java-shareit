package ru.practicum.shareitgateway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectAvailableException extends RuntimeException {
    public IncorrectAvailableException(String message) {
        super(message);
    }
}
