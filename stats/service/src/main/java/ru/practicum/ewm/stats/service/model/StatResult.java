package ru.practicum.ewm.stats.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class StatResult {
    private String app;

    private String uri;

    long hits;
}
