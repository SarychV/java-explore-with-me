package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.users.UserRepository;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.stats.client.StatsClient;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final RestTemplateBuilder builder;
    private final String serverUrl;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    public EventServiceImpl(CategoryRepository categoryRepository,
                            UserRepository userRepository,
                            EventRepository eventRepository,
                            RequestRepository requestRepository,
                            RestTemplateBuilder restBuilder,
                            @Value("${stats-server.url}") String url
                            ) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.builder = restBuilder;
        this.serverUrl = url;
        this.statsClient = new StatsClient(serverUrl, builder);
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

        log.info("eventRepository.save() was invoked with arguments initiator={}, category={}, eventDto={}",
                initiator, category, eventDto);
        Event returnedEvent = eventRepository.save(event);
        result = EventMapper.toEventDtoOut(returnedEvent, 0, 0);
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

        // Необходимо получить дополнительную информацию по каждому событию, это:
        // - количество подтвержденных запросов
        // - статистику запросов по каждому событию

        // Информацию по количеству подтвержденных запросов лучше запросить одним обращением к базе
        // Статистику получить одним запросом ?

        // Подготовить List<Long> ids событий, для которых нужна информация
        List<Long> eventIds = events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList());

        // Получить информацию по количеству подтвержденных запросов для каждого события из списка
        // Long - идентификатор события, Integer - количество подтвержденных запросов
        Map<Long, Integer> confirmedRequestsByEventIds = requestRepository.getConfirmedRequestCountsByEventIds(
                eventIds, RequestStatus.CONFIRMED);

        // Получить статистику просмотров событий
        // Long - идентификатор события, Long - количество просмотров события
        Map<Long, Long> stats = queryStatisticsByEventsVisiting(eventIds);

        // Встроить в EventDtoOut информацию по количеству подтвержденных запросов и статистику просмотров
        List<EventShortDtoOut> result = new ArrayList<>();
        for (Event event : events) {
            long eventId = event.getId();
            Integer counter = confirmedRequestsByEventIds.get(eventId);
            if (counter == null) {
                counter = Integer.valueOf(0);
            }
            Long statCounter = stats.get(eventId);
            if (statCounter == null) {
                statCounter = Long.valueOf(0L);
            }
            result.add(EventMapper.toEventShortDtoOut(event, counter, statCounter));
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

        long confirmedRequests = requestRepository.getHowManyRequestsHaveStatusForEvent(
                eventId, RequestStatus.CONFIRMED);
        long views = queryStatisticsByEventsVisiting(List.of(eventId)).get(eventId);
        EventDtoOut result = EventMapper.toEventDtoOut(event, confirmedRequests, views);
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
            throw new BadConditionException(
                    String.format("Only pending or canceled events can be changed"));
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

        long confirmedRequests = requestRepository.getHowManyRequestsHaveStatusForEvent(
                eventId, RequestStatus.CONFIRMED);
        long views = queryStatisticsByEventsVisiting(List.of(eventId)).get(eventId);
        EventDtoOut result = EventMapper.toEventDtoOut(event, confirmedRequests, views);
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
            startDate = LocalDateTime.MIN;
        }
        if (rangeEnd != null) {
            endDate = LocalDateTime.from(Constant.DATE_TIME_WHITESPACE.parse(rangeEnd));
        } else {
            endDate = LocalDateTime.MAX;
        }

        Sort sortByEventDate = Sort.by(Sort.Direction.ASC,"eventDate");
        Pageable page = PageRequest.of(from, size, sortByEventDate);
        Page<Event> pageSelection =
                eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                        userIds, states, categoryIds, startDate, endDate, page);

        List<Event> events = pageSelection.stream().collect(Collectors.toList());

        // Необходимо получить дополнительную информацию по каждому событию, это:
        // - количество подтвержденных запросов
        // - статистику запросов по каждому событию

        // Подготовить List<Long> ids событий, для которых нужна информация
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Получить информацию по количеству подтвержденных запросов для каждого события из списка
        // Long - идентификатор события, Integer - количество подтвержденных запросов
        Map<Long, Integer> confirmedRequestsByEventIds = requestRepository.getConfirmedRequestCountsByEventIds(
                eventIds, RequestStatus.CONFIRMED);

        // Получить статистику просмотров событий
        // Long - идентификатор события, Long - количество просмотров события
        Map<Long, Long> stats = queryStatisticsByEventsVisiting(eventIds);

        // Встроить в EventDtoOut информацию по количеству подтвержденных запросов и статистику просмотров
        List<EventDtoOut> result = new ArrayList<>();
        for (Event event : events) {
            long eventId = event.getId();
            Integer counter = confirmedRequestsByEventIds.get(eventId);
            if (counter == null) {
                counter = Integer.valueOf(0);
            }
            Long statCounter = stats.get(eventId);
            if (statCounter == null) {
                statCounter = Long.valueOf(0L);
            }
            result.add(EventMapper.toEventDtoOut(event, counter, statCounter));
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
        willBeLaterInNHours(updatedEvent.getEventDate(), 1);

        // Сменить статус события и установить время публикации в случае публикации
        if (stateAction == StateAction.PUBLISH_EVENT) {
            updatedEvent.setState(EventState.PUBLISHED);
            updatedEvent.setPublishedOnDate(LocalDateTime.now());
        }

        if (stateAction == StateAction.REJECT_EVENT) {
            updatedEvent.setState(EventState.CANCELED);
        }

        Event returnedEvent = eventRepository.save(updatedEvent);

        long confirmedRequests = requestRepository.getHowManyRequestsHaveStatusForEvent(
                eventId, RequestStatus.CONFIRMED);
        long views = queryStatisticsByEventsVisiting(List.of(eventId)).get(eventId);
        EventDtoOut result = EventMapper.toEventDtoOut(event, confirmedRequests, views);
        return result;
    }

    /*
    - в выдаче должны быть только опубликованные события
    - текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    + если в запросе не указан диапазон дат [rangeStart-rangeEnd],
    то нужно выгружать события, которые произойдут позже текущей даты и времени
    - информация о каждом событии должна включать в себя количество просмотров
    и количество уже одобренных заявок на участие
    - информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
    нужно сохранить в сервисе статистики
    */
    @Override
    public List<EventShortDtoOut> findPublishedEvents(String text, List<Long> categoryIds, boolean paid,
                                  String rangeStart, String rangeEnd, boolean onlyAvailable,
                                  StateSorting sort, int from, int size) {
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
            rangeEndDate = LocalDateTime.MAX;
        }
        if (rangeEndDate.isBefore(rangeStartDate)) {
            throw new BadRequestException("Parameter rangeStart should have a value before rangeEnd");
        }

        Sort sortByEventDate = Sort.by(Sort.Direction.ASC, "eventDate");
        Pageable page = PageRequest.of(from/size, size, sortByEventDate);


        return List.of();
    }

    @Override
    public EventDtoOut findCompletePublishedEventDataByEventId(long id) {
        return null;
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
    }

    private void willBeLaterInNHours(LocalDateTime dateTime, int hours) {
        LocalDateTime now = LocalDateTime.now();
        if (!now.plusHours(hours).isBefore(dateTime)) {
            throw new BadConditionException(
                    String.format("Time %s must be %d hour(s) later than current %s",
                            dateTime.toString(), hours, now.toString()));
        }
    }

    // МЕТОД - ЗАГЛУШКА
    protected Map<Long, Long> queryStatisticsByEventsVisiting(List<Long> eventIds) {
        Map<Long, Long> result = new HashMap<>();
        for (Long id : eventIds) {
            result.put(id, id);
        }
        /*statsClient.getStatistics(
                LocalDateTime.MIN,//startDate
                LocalDateTime.now(),//endDate
                ,//uris
                false//unique
        );*/
        return result;
    }
}

// 1. Доделать queryStatisticsByEventsVisiting()