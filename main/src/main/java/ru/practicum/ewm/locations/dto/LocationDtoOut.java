package ru.practicum.ewm.locations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.model.GeoLocation;

@Setter
@Getter
@ToString
public class LocationDtoOut {
    private Long id;                // уникальный идентификатор локации

    private String name;            // название локации

    private String description;     // описание локации

    private GeoLocation location;   // географические координаты локации

    private float radius;           // радиус зоны действия локации в километрах
}
