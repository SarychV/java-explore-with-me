package ru.practicum.ewm.locations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.locations.dto.LocationDtoIn;
import ru.practicum.ewm.locations.dto.LocationDtoInUpdate;
import ru.practicum.ewm.locations.dto.LocationDtoOutAdmin;
import ru.practicum.ewm.locations.dto.LocationDtoOutPublic;
import ru.practicum.ewm.locations.dto.LocationDtoOutShort;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.model.GeoLocation;
import ru.practicum.ewm.statistic.StatisticService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final StatisticService statisticService;
    private final EventRepository eventRepository;

    public LocationServiceImpl(LocationRepository locationRepository,
                                StatisticService statisticService,
                                EventRepository eventRepository) {
        this.locationRepository = locationRepository;
        this.statisticService = statisticService;
        this.eventRepository = eventRepository;
    }

    @Override
    public LocationDtoOutAdmin addLocation(LocationDtoIn locationDtoIn) {
        Location newLocation = LocationMapper.toLocation(locationDtoIn);

        List<Event> events = eventRepository.findEventsInLocation(
                newLocation.getLat(), newLocation.getLon(), newLocation.getRadius());
        newLocation.setEvents(events);

        log.info("locationRepository.save() was invoked with arguments newLocation={}", newLocation);
        Location savedLocation = locationRepository.save(newLocation);

        LocationDtoOutAdmin result = LocationMapper.toLocationDtoOutAdmin(savedLocation);
        log.info("In AdminLocationsController was returned locationDtoOut={}", result);
        return result;
    }

    @Override
    public LocationDtoOutAdmin getLocationByIdForAdmin(long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(
                String.format("Location with id=%d was not found", locationId)));

        LocationDtoOutAdmin result = LocationMapper.toLocationDtoOutAdmin(location);
        log.info("In AdminLocationsController was returned locationDtoOutAdmin={}", result);
        return result;
    }

    @Override
    public LocationDtoOutPublic getLocationByIdForPublic(long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(
                String.format("Location with id=%d was not found", locationId)));

        List<Event> onlyPublishedEvents = filterOldAndNonPublishedEvents(location.getEvents());
        location.setEvents(onlyPublishedEvents);

        LocationDtoOutPublic result = LocationMapper.toLocationDtoOutPublic(location, statisticService);
        log.info("To PublicLocationsController was returned locationDtoOutPublic={}", result);
        return result;
    }


    // Постраничный вывод перечня локаций для всех пользователей
    @Override
    public List<LocationDtoOutShort> getLocations(int from, int size) {
        List<LocationDtoOutShort> result = locationRepository.findAll(PageRequest.of(from, size)).stream()
                .map(LocationMapper::toLocationDtoOutShort)
                .collect(Collectors.toList());
        log.info("To PublicLocationsController was returned locationDtoOutShort={}", result);
        return result;
    }

    @Override
    public LocationDtoOutAdmin updateLocation(long locationId, LocationDtoInUpdate locationDtoUpdate) {
        Location previousLocation = locationRepository.findById(locationId).orElseThrow(() ->
                new NotFoundException(String.format("Location with id=%d was not found", locationId)));

        Location newLocation = updateByDto(previousLocation, locationDtoUpdate);

        log.info("locationRepository.save() was invoked with arguments newLocation={}", newLocation);
        Location savedLocation = locationRepository.save(newLocation);

        LocationDtoOutAdmin result = LocationMapper.toLocationDtoOutAdmin(savedLocation);
        log.info("To AdminLocationsController was returned locationDtoOut={}", result);
        return result;
    }

    @Override
    public void deleteLocation(long locationId) {
        locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(
                    String.format("Location with id=%d was not found", locationId)));
        locationRepository.deleteById(locationId);
        log.info("Location with id={} was deleted", locationId);
    }

    // При создании и изменении события, его принадлежность зоне локации не установлена или может быть изменена
    // Данный метод путем просмотра локаций определяет принадлежность события зоне, и в случае если событие
    // принадлежит зоне, включает его в список событий локации.
    @Override
    public void updateLocationsByEvent(Event event) {
        List<Location> locations = locationRepository.findAll();
        for (Location location : locations) {
            List<Event> eventsInLocation = location.getEvents();
            if (!eventsInLocation.contains(event)
                && LocationService.distance(event.getLat(), event.getLon(), location.getLat(),
                   location.getLon()) <= location.getRadius()) {
                eventsInLocation.add(event);
            } else {
                eventsInLocation.remove(event);
            }
        }
        locationRepository.saveAll(locations);
    }

    // Обновление объекта Location информацией, переданной через dto
    private Location updateByDto(Location previousLocation, LocationDtoInUpdate dtoIn) {
        Location result = new Location();
        String newName = dtoIn.getName();
        String newDescription = dtoIn.getDescription();
        Float newLatitude = null;
        Float newLongitude = null;
        GeoLocation newGeoLocation = dtoIn.getLocation();
        Float newRadius = dtoIn.getRadius();

        result.setId(previousLocation.getId());
        result.setName(Objects.requireNonNullElseGet(newName, previousLocation::getName));

        if (newDescription == null) {
            result.setDescription(previousLocation.getDescription());
        } else result.setDescription(newDescription);

        result.setRadius(Objects.requireNonNullElseGet(newRadius, previousLocation::getRadius));

        if (newGeoLocation != null) {
            newLatitude = newGeoLocation.getLat();
            newLongitude = newGeoLocation.getLon();

            result.setLat(Objects.requireNonNullElseGet(newLatitude, previousLocation::getLat));
            result.setLon(Objects.requireNonNullElseGet(newLongitude, previousLocation::getLon));
        } else {
            result.setLat(previousLocation.getLat());
            result.setLon(previousLocation.getLon());
        }

        // Если изменились параметры зоны локации, необходимо перечитать список событий в новой зоне
        if (newLatitude != null || newLongitude != null || newRadius != null) {
            List<Event> events = eventRepository.findEventsInLocation(
                    result.getLat(), result.getLon(), result.getRadius());
            result.setEvents(events);
        }
        return result;
    }

    private List<Event> filterOldAndNonPublishedEvents(List<Event> events) {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
                .filter(event -> EventState.PUBLISHED == event.getState())
                .filter(event -> event.getEventDate().isAfter(now))
                .collect(Collectors.toList());
    }
}