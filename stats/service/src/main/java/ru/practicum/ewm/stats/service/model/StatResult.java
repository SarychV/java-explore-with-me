package ru.practicum.ewm.stats.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StatResult {
    private String app;

    private String uri;

    long hits;
}
