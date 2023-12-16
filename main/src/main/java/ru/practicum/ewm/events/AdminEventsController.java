package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDtoInUpdate;
import ru.practicum.ewm.events.dto.EventDtoOut;
import ru.practicum.ewm.events.model.EventState;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
public class AdminEventsController {
    private final EventService eventService;

    public AdminEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    // Поиск событий
    @GetMapping
    public List<EventDtoOut> eventAdminSearch(
            @RequestParam(required = false, name = "users") List<Long> userIds,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false, name = "categories") List<Long> categoryIds,
            // Дата и время, не раньше которого должны произойти события
            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}(:\\d{2}){1,2}$") String rangeStart,
            // Дата и время, не позже которого должны произойти события
            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}(:\\d{2}){1,2}$") String rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("eventService.findEventsForAdmin() was invoked with arguments userIds={}, states={}, " +
                "categoryIds={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
        return eventService.findEventsForAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
    }

    // Редактирование данных события и его статуса (отклонение/публикация)
    @PatchMapping("/{eventId}")
    public EventDtoOut eventAdminUpdate(
            @PathVariable @PositiveOrZero Long eventId,
            @RequestBody @Valid EventDtoInUpdate eventDtoInUpdate) {
        log.info("eventService.updateEventsByAdmin() was invoked with arguments eventDtoInUpdate={}",
                eventDtoInUpdate);
        return eventService.updateEventByAdmin(eventId, eventDtoInUpdate);
    }
}
