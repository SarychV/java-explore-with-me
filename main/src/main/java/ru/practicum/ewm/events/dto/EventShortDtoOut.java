package ru.practicum.ewm.events.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.users.dto.UserShortDto;

@Setter
@Getter
public class EventShortDtoOut {
    private Long id;                    // Уникальный идентификатор события

    private String title;               // Заголовок события

    private String annotation;          // Краткое описание события

    private CategoryDto category;       // Категория

    private long confirmedRequests;     // Количество одобренных заявок на участие в данном событии

    private String eventDate;           // Дата и время на которые намечено событие
                                        // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private UserShortDto initiator;     // Пользователь (краткая информация)

    private boolean paid;               // Нужно ли оплачивать участие в событии

    private long views;                 // Количество просмотров события
}
