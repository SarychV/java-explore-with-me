package ru.practicum.ewm.locations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.locations.dto.LocationDtoOutPublic;
import ru.practicum.ewm.locations.dto.LocationDtoOutShort;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/locations")
public class PublicLocationsController {
    private final LocationService locationService;

    public PublicLocationsController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<LocationDtoOutShort> getLocations(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("locationService.getLocations() was invoked with arguments from={}, size={}", from, size);

        return locationService.getLocations(from, size);
    }

    @GetMapping("/{locationId}")
    public LocationDtoOutPublic getLocationById(@PathVariable @Positive Long locationId) {
        log.info("locationService.getLocationByIdForPublic() was invoked with arguments locationId={}", locationId);

        return locationService.getLocationByIdForPublic(locationId);
    }
}
