package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInUpdate;
import ru.practicum.ewm.events.dto.EventDtoOut;
import ru.practicum.ewm.events.dto.EventShortDtoOut;
import ru.practicum.ewm.requests.RequestService;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoIn;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
public class UsersEventsController {
    private final EventService eventService;
    private final RequestService requestService;

    public UsersEventsController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoOut addEvent(
            @PathVariable @Positive Long userId,
            @RequestBody @Valid EventDtoIn eventDtoIn) {
        log.info("eventService.addEvent() was invoked with arguments userId={}, eventDtoIn={}", userId, eventDtoIn);
        return eventService.addEvent(userId, eventDtoIn);
    }

    // Получение событий, добавленных текущим пользователем
    @GetMapping()
    public List<EventShortDtoOut> getCurrentUserEvents(
            @PathVariable @Positive long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("eventService.getUserEvents() " +
                        "was invoked with arguments userId={}, from={}, size={}", userId, from, size);
        return eventService.findEventsForUser(userId, from, size);
    }

    // Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventDtoOut getCompleteEventData(@PathVariable @Positive Long userId,
                                            @PathVariable @PositiveOrZero Long eventId) {
        log.info("eventService.getCompleteEventData() was invoked with arguments userId={}, eventId={}",
                userId, eventId);
        return eventService.findEventWithCompleteDataForUser(userId, eventId);
    }

    // Изменение события, добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    public EventDtoOut updateEventData(@PathVariable @Positive Long userId,
                                       @PathVariable @PositiveOrZero Long eventId,
                                       @RequestBody @Valid EventDtoInUpdate eventDtoInUpdate) {
        log.info("eventService.changeEventData() was invoked with arguments userId={}, eventId={}",
                userId, eventId);
        return eventService.updateEventByUser(userId, eventId, eventDtoInUpdate);
    }

    // Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{eventId}/requests")
    public List<RequestDto> giveUserTheRequestsForEvent(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId) {
        log.info("requestService.giveInitiatorTheRequestsForHisEvents() " +
                        "was invoked with arguments userId={}, eventId={}", userId, eventId);
        return requestService.giveUserEventRequests(userId, eventId);
    }

    // Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests")
    public RequestsAndStatusDtoOut changeRequestStatusForEvent(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId,
            @RequestBody @Valid RequestsAndStatusDtoIn requestsAndStatusDtoIn) {
        log.info("requestService.changeRequestStatusForEvent() " +
                "was invoked with arguments userId={}, eventId={}, requestsAndStatusDtoIn={}",
                userId, eventId, requestsAndStatusDtoIn);
        return requestService.changeEventRequestStatus(userId, eventId, requestsAndStatusDtoIn);
    }

}
