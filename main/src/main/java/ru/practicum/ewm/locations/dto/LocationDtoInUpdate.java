package ru.practicum.ewm.locations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.model.GeoLocation;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Setter
@Getter
@ToString
public class LocationDtoInUpdate {
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    @Valid
    private GeoLocation location;

    @Min(0)
    @Max(20500)
    private Float radius;
}
