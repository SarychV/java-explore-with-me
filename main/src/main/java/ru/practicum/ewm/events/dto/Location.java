package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ToString
@Getter
@AllArgsConstructor
public class Location {
    @Min(-90)
    @Max(90)
    private float lat;  // latitude - широта места проведения мероприятия
    @Min(-180)
    @Max(180)
    private float lon;  // longitude - долгота места проведения мероприятия
}
