package ru.practicum.ewm.events;

import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInUpdate;
import ru.practicum.ewm.events.dto.EventDtoOut;
import ru.practicum.ewm.events.dto.EventShortDtoOut;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.StateSorting;

import java.util.List;

public interface EventService {
    EventDtoOut addEvent(long userId, EventDtoIn eventDto);

    List<EventShortDtoOut> findEventsForUser(long userId, int from, int size);

    EventDtoOut findEventWithCompleteDataForUser(long userId, long eventId);

    EventDtoOut updateEventByUser(long userId, long eventId, EventDtoInUpdate eventDto);

    List<EventDtoOut> findEventsForAdmin(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                         String rangeStart, String rangeEnd, int from, int size);

    EventDtoOut updateEventByAdmin(long eventId, EventDtoInUpdate eventDtoInUpdate);

    List<EventShortDtoOut> findPublishedEvents(String text, List<Long> categoryIds, boolean paid,
                                               String rangeStart, String rangeEnd, boolean onlyAvailable,
                                               StateSorting sort, int from, int size, String ip, String uri);

    EventDtoOut findCompletePublishedEventDataByEventId(long id, String ip, String uri);
}
