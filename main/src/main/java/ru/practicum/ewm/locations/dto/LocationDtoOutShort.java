package ru.practicum.ewm.locations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LocationDtoOutShort extends LocationDtoOut {
    // количество событий, координаты которых входят в зону локации
    private long events;
}
