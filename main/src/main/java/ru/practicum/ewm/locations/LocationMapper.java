package ru.practicum.ewm.locations;

import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.locations.dto.LocationDtoIn;
import ru.practicum.ewm.locations.dto.LocationDtoOutAdmin;
import ru.practicum.ewm.locations.dto.LocationDtoOutPublic;
import ru.practicum.ewm.locations.dto.LocationDtoOutShort;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.model.GeoLocation;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationMapper {
    public static Location toLocation(LocationDtoIn dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setLat(dto.getLocation().getLat());
        location.setLon(dto.getLocation().getLon());
        location.setRadius(dto.getRadius());
        return location;
    }

    public static LocationDtoOutAdmin toLocationDtoOutAdmin(Location location) {
        LocationDtoOutAdmin result = new LocationDtoOutAdmin();
        result.setId(location.getId());
        result.setName(location.getName());
        result.setDescription(location.getDescription());
        result.setLocation(new GeoLocation(location.getLat(), location.getLon()));
        result.setRadius(location.getRadius());

        List<Event> events = location.getEvents();
        if (events != null) {
            result.setEvents(events.stream()
                    .map(EventMapper::toEventLocationDtoOutAdmin)
                    .collect(Collectors.toList()));
        } else {
            result.setEvents(List.of());
        }
        return result;
    }

    public static LocationDtoOutPublic toLocationDtoOutPublic(Location location, StatisticService statisticService) {
        LocationDtoOutPublic result = new LocationDtoOutPublic();
        result.setId(location.getId());
        result.setName(location.getName());
        result.setDescription(location.getDescription());
        result.setLocation(new GeoLocation(location.getLat(), location.getLon()));
        result.setRadius(location.getRadius());

        List<Event> events = location.getEvents();
        if (events != null) {
            List<Long> eventIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, true);
            result.setEvents(events.stream()
                    .map(e -> EventMapper.toEventLocationDtoOutUser(e, stats.get(e.getId())))
                    .collect(Collectors.toList()));
        } else {
            result.setEvents(List.of());
        }
        return result;
    }

    public static LocationDtoOutShort toLocationDtoOutShort(Location location) {
        LocationDtoOutShort result = new LocationDtoOutShort();
        result.setId(location.getId());
        result.setName(location.getName());
        result.setDescription(location.getDescription());
        result.setLocation(new GeoLocation(location.getLat(), location.getLon()));
        result.setRadius(location.getRadius());
        result.setEvents(location.getEvents().size());
        return result;
    }
}
