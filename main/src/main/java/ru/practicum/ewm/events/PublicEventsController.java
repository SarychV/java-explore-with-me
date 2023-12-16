package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDtoOut;
import ru.practicum.ewm.events.dto.EventShortDtoOut;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.StateSorting;
import ru.practicum.ewm.requests.RequestService;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@Validated
public class PublicEventsController {
    private final EventService eventService;

    public PublicEventsController(EventService eventService) {
        this.eventService = eventService;
    }

    // Получение событий с возможностью фильтрации
    @GetMapping
    public List<EventShortDtoOut> searchPublishedEvents(
            // Текст для поиска в содержимом аннотации и подробном описании события
            @RequestParam(required = false)
            @Size(min = 1, max = 7000)
            String text,

            // Список идентификаторов категорий, в которых будет вестись поиск
            @RequestParam(required = false, name = "categories")
            List<Long> categoryIds,

            // Поиск только платных/бесплатных событий
            @RequestParam(required = false)
            boolean paid,

            // Дата и время, не раньше которого должны произойти события
            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}(:\\d{2}){1,2}$")
            String rangeStart,

            // Дата и время, не позже которого должны произойти события
            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}(:\\d{2}){1,2}$")
            String rangeEnd,

            // Только события, у которых не исчерпан лимит запросов на участие
            @RequestParam(required = false, defaultValue = "false")
            boolean onlyAvailable,

            // Вариант сортировки: по дате события или по количеству просмотров
            // Enum: "EVENT_DATE" "VIEWS"
            @RequestParam(required = false)
            StateSorting sort,

            // Количество событий, которые нужно пропустить для формирования текущего набора
            @RequestParam(required = false, defaultValue = "0")
            @PositiveOrZero
            int from,

            // Количество событий в наборе
            @RequestParam(required = false, defaultValue = "10")
            @Positive
            int size) {
        log.info("eventService.findPublishedEvents() was invoked with arguments text={}, " +
                        "categoryIds={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                        "sort={}, from={}, size={}",
                text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findPublishedEvents(text, categoryIds, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    // Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("/{id}")
    public EventDtoOut findCompleteEventDataByEventId(@PathVariable @Positive long id) {
        log.info("eventService.findCompleteEventDataById) was invoked with arguments id={}", id);
        return eventService.findCompletePublishedEventDataByEventId(id);
    }
}

