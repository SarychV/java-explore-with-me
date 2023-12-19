package ru.practicum.ewm.events;

import ru.practicum.ewm.Constant;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.users.UserMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.categories.CategoryMapper;

import java.time.LocalDateTime;

public class EventMapper {
    public static Event toEvent(EventDtoIn eventDtoIn, Category category, User initiator) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(eventDtoIn.getEventDate()));
        Location location = eventDtoIn.getLocation();
        Event result = new Event();

        result.setTitle(eventDtoIn.getTitle());
        result.setAnnotation(eventDtoIn.getAnnotation());
        result.setDescription(eventDtoIn.getDescription());
        result.setLon(location.getLon());
        result.setLat(location.getLat());
        result.setPaid(eventDtoIn.isPaid());
        result.setParticipantLimit(eventDtoIn.getParticipantLimit());
        result.setCreatedOnDate(now);
        result.setEventDate(eventDate);
        result.setRequestModeration(eventDtoIn.isRequestModeration());
        result.setState(EventState.PENDING);
        result.setCategory(category);
        result.setInitiator(initiator);
        return result;
    }

    public static EventDtoOut toEventDtoOut(Event event, long views) {
        EventDtoOut result = new EventDtoOut();
        result.setId(event.getId());
        result.setTitle(event.getTitle());
        result.setAnnotation(event.getAnnotation());
        result.setDescription(event.getDescription());
        result.setLocation(new Location(event.getLat(), event.getLon()));
        result.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        result.setCreatedOn(event.getCreatedOnDate().format(Constant.DATE_TIME_WHITESPACE));
        result.setEventDate(event.getEventDate().format(Constant.DATE_TIME_WHITESPACE));
        if (event.getPublishedOnDate() != null) {
            result.setPublishedOn(event.getPublishedOnDate().format(Constant.DATE_TIME_WHITESPACE));
        } else {
            result.setPublishedOn(null);
        }
        result.setPaid(event.isPaid());
        result.setParticipantLimit(event.getParticipantLimit());
        result.setRequestModeration(event.isRequestModeration());
        result.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        result.setConfirmedRequests(event.getConfirmedRequests());
        result.setState(event.getState().name());
        result.setViews(views);
        return result;
    }

    public static EventShortDtoOut toEventShortDtoOut(Event event, long views) {
        EventShortDtoOut result = new EventShortDtoOut();
        result.setId(event.getId());
        result.setTitle(event.getTitle());
        result.setAnnotation(event.getAnnotation());
        result.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        result.setConfirmedRequests(event.getConfirmedRequests());
        result.setEventDate(event.getEventDate().format(Constant.DATE_TIME_WHITESPACE));
        result.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        result.setPaid(event.isPaid());
        result.setViews(views);
        return result;
    }

    public static Event toUpdatedEvent(Event event, EventDtoInUpdate eventDto, Category category) {
        Event newEvent = new Event();

        newEvent.setId(event.getId());
        newEvent.setCreatedOnDate(event.getCreatedOnDate());
        newEvent.setInitiator(event.getInitiator());
        newEvent.setState(event.getState());
        newEvent.setConfirmedRequests(event.getConfirmedRequests());

        if (eventDto.getAnnotation() == null) {
            newEvent.setAnnotation(event.getAnnotation());
        } else {
            newEvent.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() == null) {
            newEvent.setCategory(event.getCategory());
        } else {
            newEvent.setCategory(category);
        }

        if (eventDto.getDescription() == null) {
            newEvent.setDescription(event.getDescription());
        } else {
            newEvent.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() == null) {
            newEvent.setEventDate(event.getEventDate());
        } else {
            newEvent.setEventDate(LocalDateTime.from(
                    Constant.DATE_TIME_WHITESPACE.parse(eventDto.getEventDate())));
        }

        if (eventDto.getLocation() == null) {
            newEvent.setLat(event.getLat());
            newEvent.setLon(event.getLon());
        } else {
            newEvent.setLat(eventDto.getLocation().getLat());
            newEvent.setLon(eventDto.getLocation().getLon());
        }

        if (eventDto.getPaid() == null) {
            newEvent.setPaid(event.isPaid());
        } else {
            newEvent.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() == null) {
            newEvent.setParticipantLimit(event.getParticipantLimit());
        } else {
            newEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() == null) {
            newEvent.setRequestModeration(event.isRequestModeration());
        } else {
            newEvent.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() == null) {
            newEvent.setTitle(event.getTitle());
        } else {
            newEvent.setTitle(eventDto.getTitle());
        }

        return newEvent;
    }
}
