package ru.practicum.ewm.locations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.locations.dto.LocationDtoInUpdate;
import ru.practicum.ewm.locations.dto.LocationDtoOutAdmin;
import ru.practicum.ewm.locations.dto.LocationDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/locations")
public class AdminLocationsController {
    private final LocationService locationService;

    public AdminLocationsController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDtoOutAdmin addLocation(@RequestBody @Valid LocationDtoIn locationDtoIn) {
        log.info("locationService.addLocation() was invoked with arguments locationDtoIn={}",
                locationDtoIn);
        return locationService.addLocation(locationDtoIn);
    }

    @GetMapping("/{locationId}")
    public LocationDtoOutAdmin getLocationById(@PathVariable @Positive long locationId) {
        log.info("locationService.readLocationById() was invoked with arguments locationId={}",
                locationId);
        return locationService.getLocationByIdForAdmin(locationId);
    }

    @PatchMapping("/{locationId}")
    public LocationDtoOutAdmin updateLocation(
            @PathVariable @Positive long locationId,
            @RequestBody @Valid LocationDtoInUpdate locationUpdate) {
        // Validation
        if (locationUpdate.getName() == null && locationUpdate.getDescription() == null
                && locationUpdate.getLocation() == null && locationUpdate.getRadius() == null) {
            throw new BadRequestException("Request has no parameters for update");
        }

        log.info("locationService.updateLocation() was invoked with arguments locationId={}, locationDto={}",
                locationId, locationUpdate);
        return locationService.updateLocation(locationId, locationUpdate);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable @Positive long locationId) {
        log.info("locationService.deleteLocation() was invoked with locationId={}", locationId);
        locationService.deleteLocation(locationId);
    }
}


