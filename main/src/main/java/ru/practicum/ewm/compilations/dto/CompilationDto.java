package ru.practicum.ewm.compilations.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
public class CompilationDto {
    private long id;                // Уникальный идентификатор подборки

    @Size(min = 1, max = 50)
    private String title;           // Заголовок подборки

    private List<Long> events;      // Перечень событий в подборке

    private Boolean pinned;         // Закреплена ли подборка на главной странице сайта
}
