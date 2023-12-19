package ru.practicum.ewm.events.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Setter
@Getter
@ToString
public class EventDtoIn {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;       // Заголовок события

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;  // Краткое описание события

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}(:\\d{2}){1,2}$")
    private String eventDate;   // Дата и время на которые намечено событие
                                // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"

    @Valid
    private Location location;          // Широта и долгота места проведения события

    private boolean paid = false;       // Нужно ли оплачивать участие в событии

    private int participantLimit = 0;   // Ограничение на количество участников
                                        // Значение 0 - означает отсутствие ограничения

    private boolean requestModeration = true;   // Нужна ли пре-модерация заявок на участие. Если true, то все заявки
                                            // будут ожидать подтверждения инициатором события. Если false - то будут
                                            // подтверждаться автоматически

    @NotNull
    @Positive
    private Long category;   // Категория, к которой относится событие
}
