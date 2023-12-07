package ru.practicum.ewm.stats.service.exception;

public class BadRequestException extends IllegalArgumentException {
    public BadRequestException() {
    }

    public BadRequestException(String msg) {
        super(msg);
    }
}
