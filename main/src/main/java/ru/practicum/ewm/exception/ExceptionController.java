package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.Constant;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    //Ошибочная обработка аргументов по аннотации @Valid
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethod(final MethodArgumentNotValidException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    //Ошибочная обработка аргументов по аннотациям из пакета javax.validations при использовании @Valid
    // в параметрах контроллеров
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethod(final MethodArgumentTypeMismatchException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    // Обработка нарушения ограничений по значению параметров в контроллере по аннотации @Validated
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethod(final ConstraintViolationException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    // Обработка нарушения ограничений по значению параметров в контроллере по аннотации @Validated
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethod(final BadRequestException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    // Обработка нарушения по отсутствию обязательного параметра в аргументах контроллера
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethod(final MissingServletRequestParameterException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.name(),
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    //Обработка нарушения уникальности имени (пользователя, категории) при создании объекта
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleMethod(final DataIntegrityViolationException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.CONFLICT.name(),
                "reason", "Integrity constraint has been violated.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    //Обработка неверной даты создания события
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleMethod(final BadConditionException e) {
        log.warn(e.toString());
        return Map.of(
                "status", "FORBIDDEN",
                "reason", "For the requested operation the conditions are not met.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    //Отсутствие объекта при удалении (пользователя, категории)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody Map<String, String> handleMethod(final NotFoundException e) {
        log.warn(e.toString());
        return Map.of(
                "status", HttpStatus.NOT_FOUND.name(),
                "reason", "The required object was not found.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(Constant.DATE_TIME_WHITESPACE));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMethod(final Exception e) {
        log.error(e.toString());
        return Map.of("error", e.getMessage());
    }
}
