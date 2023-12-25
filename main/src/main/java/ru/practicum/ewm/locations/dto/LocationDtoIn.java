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
public class LocationDtoIn {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    @Valid
    @NotNull
    private GeoLocation location;

    @Min(0)
    @Max(20500)
    @NotNull
    private Float radius;
}
