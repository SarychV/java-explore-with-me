package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@Getter
@AllArgsConstructor
public class StatResultDto {
    private String app;

    private String uri;

    private int hits;
}
