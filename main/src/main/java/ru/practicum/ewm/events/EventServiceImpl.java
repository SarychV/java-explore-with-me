package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.Constant;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInUpdate;
import ru.practicum.ewm.events.dto.EventDtoOut;
import ru.practicum.ewm.events.dto.EventShortDtoOut;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.StateAction;
import ru.practicum.ewm.events.model.StateSorting;
import ru.practicum.ewm.exception.BadConditionException;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.locations.LocationService;
import ru.practicum.ewm.statistic.StatisticService;
import ru.practicum.ewm.users.UserRepository;
import ru.practicum.ewm.users.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final LocationService locationService;

    public EventServiceImpl(CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            EventRepository eventRepository,
                            StatisticService statisticService,
                            LocationService locationService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.statisticService =  statisticService;
        this.locationService = locationService;
    }

    @Override
    public EventDtoOut addEvent(long userId, EventDtoIn eventDto) {
        EventDtoOut result;
        User initiator = checkUser(userId);
        long categoryId = eventDto.getCategory();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", categoryId)));

        Event event = EventMapper.toEvent(eventDto, category, initiator);
        willBeLaterInNHours(event.getEventDate(), 2);
        event.setConfirmedRequests(0L);

        log.info("eventRepository.save() was invoked with arguments initiator={}, category={}, eventDto={}",
                initiator, category, eventDto);
        Event returnedEvent = eventRepository.save(event);
        locationService.updateLocationsByEvent(returnedEvent);
        result = EventMapper.toEventDtoOut(returnedEvent, 0);
        log.info("In UsersEventsController was returned eventDtoOut={}", result);
        return result;
    }

    @Override
    public List<EventShortDtoOut> findEventsForUser(long userId, int from, int size) {
        User user = checkUser(userId);
        Pageable page = PageRequest.of(from, size);
        Page<Event> eventsPage = eventRepository.findAllByInitiator(user, page);
        // Список событий из базы событий, который создал пользователь userId
        List<Event> events = eventsPage.stream().collect(Collectors.toList());

        // Необходимо получить статистику запросов по каждому событию
        // Подготовить List<Long> ids событий, для которых нужна информация
        List<Long> eventIds = events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList());

        // Получить статистику просмотров событий
        // Long - идентификатор события, Long - количество просмотров события
        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, false);

        // Встроить в EventDtoOut статистику просмотров
        List<EventShortDtoOut> result = new ArrayList<>();
        for (Event event : events) {
            result.add(EventMapper.toEventShortDtoOut(event, stats.get(event.getId())));
        }
        return result;
    }

    @Override
    public EventDtoOut findEventWithCompleteDataForUser(long userId, long eventId) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        // пользователь должен быть инициатором заявки, чтобы запрос обработался
        if (event.getInitiator().getId() != userId) {
            throw new BadConditionException(
                    String.format("A userId=%d is not an initiator of this eventId=%d", userId, eventId));
        }

        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(List.of(eventId), false);
        long views = 0;
        if (stats.size() == 1) {
            views = stats.get(eventId);
        }
        EventDtoOut result = EventMapper.toEventDtoOut(event, views);
        return result;
    }

    /*
    + изменить можно только отмененные события или события в состоянии
    ожидания модерации (Ожидается код ошибки 409)
    + дата и время на которые намечено событие не может быть раньше,
     чем через два часа от текущего момента (Ожидается код ошибки 409)
    */
    @Override
    public EventDtoOut updateEventByUser(long userId, long eventId, EventDtoInUpdate eventDto) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        // пользователь должен быть инициатором заявки, чтобы запрос обработался
        if (event.getInitiator().getId() != userId) {
            throw new BadConditionException(
                    String.format("A userId=%d is not an initiator of this eventId=%d", userId, eventId));
        }

        // изменить можно только отмененные события или события в состоянии ожидания модерации
        EventState eventState = event.getState();
        if (eventState != EventState.PENDING && eventState != EventState.CANCELED) {
            throw new BadConditionException("Only pending or canceled events can be changed");
        }

        // если категория была изменена, необходимо подготовить объект с новой категорией
        Category newCategory = null;
        Long newCategoryId = eventDto.getCategory();
        if (newCategoryId != null) {
            newCategory = categoryRepository.findById(newCategoryId).orElseThrow(() -> new NotFoundException(
                    String.format("Category with id=%d was not found", newCategoryId)));
        }

        Event updatedEvent = EventMapper.toUpdatedEvent(event, eventDto, newCategory);
        willBeLaterInNHours(updatedEvent.getEventDate(), 2);

        // Изменение состояния объекта согласно запросу (в случае наличия требования)
        StateAction stateAction = eventDto.getStateAction();
        if (stateAction == StateAction.SEND_TO_REVIEW) {
            updatedEvent.setState(EventState.PENDING);
        } else if (stateAction == StateAction.CANCEL_REVIEW) {
            updatedEvent.setState(EventState.CANCELED);
        }

        Event returnedEvent = eventRepository.save(updatedEvent);
        if (eventDto.getLocation() != null) {
            locationService.updateLocationsByEvent(returnedEvent);
        }

        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(List.of(eventId), false);
        long views = 0;
        if (stats.size() == 1) {
            views = stats.get(eventId);
        }

        EventDtoOut result = EventMapper.toEventDtoOut(returnedEvent, views);
        return result;
    }

    @Override
    public List<EventDtoOut> findEventsForAdmin(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                         String rangeStart, String rangeEnd, int from, int size) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (rangeStart != null) {
            startDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(rangeStart));
        } else {
            startDate = Constant.DATE_MIN;
        }
        if (rangeEnd != null) {
            endDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(rangeEnd));
        } else {
            endDate = Constant.DATE_MAX;
        }

        Sort sortByEventDate = Sort.by(Sort.Direction.ASC,"eventDate");
        Pageable page = PageRequest.of(from, size, sortByEventDate);
        Page<Event> pageSelection =
                //eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                eventRepository.findEventsForAdmin(
                        userIds, states, categoryIds, startDate, endDate, page);

        List<Event> events = pageSelection.stream().collect(Collectors.toList());

        // Подготовить List<Long> ids событий, для которых нужна информация
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Получить статистику просмотров событий
        // Long - идентификатор события, Long - количество просмотров события
        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, false);

        // Встроить в EventDtoOut статистику просмотров событий
        List<EventDtoOut> result = new ArrayList<>();
        for (Event event : events) {
            result.add(EventMapper.toEventDtoOut(event, stats.get(event.getId())));
        }
        return result;
    }

    /*
    + дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
    + событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
    + событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
    */
    @Override
    public EventDtoOut updateEventByAdmin(long eventId, EventDtoInUpdate eventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        // изменить можно только отмененные события или события в состоянии ожидания модерации
        EventState eventState = event.getState();
        StateAction stateAction = eventDto.getStateAction();

        if (stateAction == StateAction.PUBLISH_EVENT && eventState != EventState.PENDING) {
            throw new BadConditionException(
                    String.format("Cannot publish the event because it's not in the right state: %s",
                    eventState.name()));
        }

        if (stateAction == StateAction.REJECT_EVENT && eventState == EventState.PUBLISHED) {
            throw new BadConditionException(
                    String.format("Cannot reject the event because it's not in the right state: %s",
                            eventState.name()));
        }

        // если категория была изменена, необходимо подготовить объект с новой категорией
        Category newCategory = null;
        Long newCategoryId = eventDto.getCategory();
        if (newCategoryId != null) {
            newCategory = categoryRepository.findById(newCategoryId).orElseThrow(() -> new NotFoundException(
                    String.format("Category with id=%d was not found", newCategoryId)));
        }

        Event updatedEvent = EventMapper.toUpdatedEvent(event, eventDto, newCategory);

        if (updatedEvent.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event time should be later than current time");
        }

        // Сменить статус события и установить время публикации в случае публикации
        if (stateAction == StateAction.PUBLISH_EVENT) {
            LocalDateTime publishDate = LocalDateTime.now();
            LocalDateTime eventDate = updatedEvent.getEventDate();
            if (publishDate.isAfter(eventDate.minusHours(1))) {
                throw new BadRequestException(
                        String.format("Event time should be 1 hour later than publication time  %s", publishDate));
            }
            updatedEvent.setState(EventState.PUBLISHED);
            updatedEvent.setPublishedOnDate(publishDate);
        }

        if (stateAction == StateAction.REJECT_EVENT) {
            updatedEvent.setState(EventState.CANCELED);
        }

        Event returnedEvent = eventRepository.save(updatedEvent);
        if (eventDto.getLocation() != null) {
            locationService.updateLocationsByEvent(returnedEvent);
        }

        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(List.of(eventId), true);
        long views = 0;
        if (stats.size() == 1) {
            views = stats.get(eventId);
        }

        EventDtoOut result = EventMapper.toEventDtoOut(returnedEvent, views);
        return result;
    }

    /*
    + в выдаче должны быть только опубликованные события
    + текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    + если в запросе не указан диапазон дат [rangeStart-rangeEnd],
    то нужно выгружать события, которые произойдут позже текущей даты и времени
    + информация о каждом событии должна включать в себя количество просмотров
    и количество уже одобренных заявок на участие
    + информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
    нужно сохранить в сервисе статистики
    */
    @Override
    public List<EventShortDtoOut> findPublishedEvents(String text, List<Long> categoryIds, boolean paid,
                                  String rangeStart, String rangeEnd, boolean onlyAvailable,
                                  StateSorting sort, int from, int size, String ip, String uri) {
        LocalDateTime rangeStartDate;
        LocalDateTime rangeEndDate;

        if (rangeStart != null) {
            rangeStartDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(rangeStart));
        } else {
            rangeStartDate = LocalDateTime.now();
        }

        if (rangeEnd != null) {
            rangeEndDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(rangeEnd));
        } else {
            rangeEndDate = Constant.DATE_MAX;
        }
        if (rangeEndDate.isBefore(rangeStartDate)) {
            throw new BadRequestException("Parameter rangeStart should have a value before rangeEnd");
        }

        // сортировка по умолчанию по eventDate, после получения списка событий будет выполнена пересортировка,
        // if (sort == StateSorting.VIEWS): true
        Sort sortByEventDate = Sort.by(Sort.Direction.DESC, "eventDate");
        Pageable page = PageRequest.of(from / size, size, sortByEventDate);

        Page<Event> pageSelection = eventRepository.findPublishedEventsByFilter(
                text, categoryIds, paid, EventState.PUBLISHED, rangeStartDate, rangeEndDate,
                onlyAvailable, page);

        List<Event> events = pageSelection.stream().collect(Collectors.toList());

        // Подготовить List<Long> ids событий, для которых нужна информация
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Получить статистику просмотров событий
        // Long - идентификатор события, Long - количество просмотров события
        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, false);

        // Встроить в EventShortDtoOut статистику просмотров событий
        List<EventShortDtoOut> result = new ArrayList<>();
        for (Event event : events) {
            result.add(EventMapper.toEventShortDtoOut(event, stats.get(event.getId())));
        }

        if (sort == StateSorting.VIEWS) {
            result.sort(Comparator.comparingLong(EventShortDtoOut::getViews));
        }
        statisticService.sendStatistics(ip, uri);
        return result;
    }

    /*
    + событие должно быть опубликовано
    + информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    + информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    */
    @Override
    public EventDtoOut findCompletePublishedEventDataByEventId(long eventId, String ip, String uri) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=%d was not published");
        }

        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(List.of(eventId), true);

        long views = 0;
        if (stats.size() == 1) {
            views = stats.get(eventId);
        }
        EventDtoOut result = EventMapper.toEventDtoOut(event, views);

        statisticService.sendStatistics(ip, uri);
        return result;
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
    }

    private void willBeLaterInNHours(LocalDateTime dateTime, int hours) {
        LocalDateTime now = LocalDateTime.now();
        if (!now.plusHours(hours).isBefore(dateTime)) {
            throw new BadRequestException(
                    String.format("Time %s must be %d hour(s) later than current %s",
                            dateTime.toString(), hours, now.toString()));
        }
    }
}