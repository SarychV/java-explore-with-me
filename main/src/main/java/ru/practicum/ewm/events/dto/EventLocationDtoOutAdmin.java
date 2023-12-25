package ru.practicum.ewm.events.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.model.GeoLocation;
import ru.practicum.ewm.users.dto.UserShortDto;

@Setter
@Getter
public class EventLocationDtoOutAdmin {
    private Long id;                    // Уникальный идентификатор события

    private String title;               // Заголовок события

    private String annotation;          // Краткое описание события

    private CategoryDto category;       // Категория

    private String eventDate;           // Дата и время на которые намечено событие
                                        // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private UserShortDto initiator;     // Пользователь (краткая информация)

    private EventState state;           // Состояние события

    private GeoLocation location;       // Географические координаты места проведения события

}
