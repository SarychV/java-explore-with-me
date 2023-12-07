package ru.practicum.ewm.stats.map;

import lombok.extern.slf4j.Slf4j;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toLocalDateTime(String dateTime) {
        return LocalDateTime.from(formatter.parse(dateTime));
    }

    public static String localDateTimeToStringWithEncoding(LocalDateTime dateTime) {
        try {
            return URLEncoder.encode(dateTime.format(formatter), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            log.error("Для преобразования времени используется неподдерживаемая кодировка 'UTF-8'");
            e.printStackTrace();
        }
        return "";
    }
}