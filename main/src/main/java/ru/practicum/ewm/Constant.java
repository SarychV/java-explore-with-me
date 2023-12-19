package ru.practicum.ewm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String SERVICE_NAME = "ewm-main-service";

    public static final DateTimeFormatter DATE_TIME_WHITESPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final LocalDateTime DATE_MIN = LocalDateTime.of(
            LocalDate.of(0,1,1), LocalTime.of(0,0));

    public static final LocalDateTime DATE_MAX = LocalDateTime.of(
            LocalDate.of(10000, 1,1), LocalTime.of(0,0));
}
