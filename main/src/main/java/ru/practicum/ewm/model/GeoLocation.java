package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ToString
@Getter
@AllArgsConstructor
public class GeoLocation {
    @Min(-90)
    @Max(90)
    private Float lat;  // latitude - широта места проведения мероприятия

    @Min(-180)
    @Max(180)
    private Float lon;  // longitude - долгота места проведения мероприятия
}
