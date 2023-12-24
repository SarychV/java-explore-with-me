package ru.practicum.ewm.locations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.events.dto.EventLocationDtoOutUser;

import java.util.List;

@Setter
@Getter
@ToString
public class LocationDtoOutPublic extends LocationDtoOut {
    // события, координаты которых входят в зону локации (отображаются только PUBLISHED события,
    // с полями, которые носят информативный характер
    private List<EventLocationDtoOutUser> events;
}
