package ru.practicum.ewm.events.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.model.GeoLocation;

@Setter
@Getter
public class EventLocationDtoOutUser extends EventShortDtoOut {
    private GeoLocation location;       // Географические координаты места проведения события
}
