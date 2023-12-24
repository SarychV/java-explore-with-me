package ru.practicum.ewm.locations;

import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.locations.dto.*;

import java.util.List;

public interface LocationService {
    LocationDtoOutAdmin addLocation(LocationDtoIn locationDtoIn);

    LocationDtoOutAdmin getLocationByIdForAdmin(long locationId);

    LocationDtoOutPublic getLocationByIdForPublic(long locationId);

    List<LocationDtoOutShort> getLocations(int from, int size);

    LocationDtoOutAdmin updateLocation(long locationId, LocationDtoInUpdate locationDtoUpdate);

    void updateLocationsByEvent(Event event);

    void deleteLocation(long locationId);
}
