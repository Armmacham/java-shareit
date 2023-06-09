package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.StateException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State convert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            String message = String.format("Unknown state: %S", source);
            throw new StateException(message);
        }
    }
}
