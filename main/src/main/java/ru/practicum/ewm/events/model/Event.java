package ru.practicum.ewm.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Уникальный идентификатор события

    private String title;       // Заголовок события

    private String annotation;  // Краткое описание события

    private String description; // Полное описание события

    @Column(name = "event_date")
    private LocalDateTime eventDate;    // Дата и время на которые намечено событие

    @Column(name = "created_on_date")
    private LocalDateTime createdOnDate;    // Дата и время создания события

    @Column(name = "published_on_date")
    private LocalDateTime publishedOnDate;  // Дата и время публикации события

    @Enumerated(EnumType.STRING)
    private EventState state;           // Состояние жизненного цикла события

    @Column(name = "longitude")
    private float lon;                  // Широта места проведения события

    @Column(name = "latitude")
    private float lat;                  // Долгота места проведения события

    boolean paid = false;               // Нужно ли оплачивать участие в событии

    int participantLimit = 0;           // Ограничение на количество участников
                                        // Значение 0 - означает отсутствие ограничения

    boolean requestModeration = true;   // Нужна ли пре-модерация заявок на участие. Если true, то все заявки
                                        // будут ожидать подтверждения инициатором события. Если false - то будут
                                        // подтверждаться автоматически

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;          // Категория, к которой относится событие

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;             // Инициатор события

    @Column(name = "confirmed_reqs")    // Количество одобренных заявок на участие в событии
    private long confirmedRequests;
}
