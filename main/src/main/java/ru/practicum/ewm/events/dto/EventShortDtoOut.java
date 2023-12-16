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

    //private String description;         // Полное описание события

    private CategoryDto categoryDto;    // Категория

    private long confirmedRequests;     // Количество одобренных заявок на участие в данном событии

    //private String createdOn;           // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private String eventDate;           // Дата и время на которые намечено событие
                                        // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private UserShortDto initiator;     // Пользователь (краткая информация)

    //private Location location;          // Широта и долгота места проведения события

    private boolean paid;               // Нужно ли оплачивать участие в событии

    //private int participantLimit;       // Ограничение на количество участников.
                                        // Значение 0 - означает отсутствие ограничения
    //private String publishedOn;         // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    //boolean requestModeration = true;   // Нужна ли пре-модерация заявок на участие

    //private String state;               // Состояние жизненного цикла события EventState

    private long views;                 // Количество просмотров события
}
