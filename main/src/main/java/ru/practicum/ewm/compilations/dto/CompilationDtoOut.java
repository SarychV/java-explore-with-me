package ru.practicum.ewm.compilations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.events.dto.EventShortDtoOut;

import java.util.List;

@Setter
@Getter
@ToString
public class CompilationDtoOut {
    private long id;                    // Уникальный идентификатор подборки

    private String title;               // Заголовок подборки

    private List<EventShortDtoOut> events;      // Перечень событий в подборке

    private boolean pinned;             // Закреплена ли подборка на главной странице сайта
}
