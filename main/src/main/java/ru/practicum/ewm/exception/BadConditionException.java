package ru.practicum.ewm.exception;

public class BadConditionException extends RuntimeException {
    public BadConditionException() {
    }

    public BadConditionException(String msg) {
        super(msg);
    }
}
