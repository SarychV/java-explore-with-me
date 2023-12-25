package ru.practicum.ewm.locations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.events.dto.EventLocationDtoOutAdmin;

import java.util.List;

@Setter
@Getter
@ToString
public class LocationDtoOutAdmin extends LocationDtoOut {
    // события, координаты которых входят в зону локации, с отображением статуса событий, но без
    // информационных полей
    private List<EventLocationDtoOutAdmin> events;
}
