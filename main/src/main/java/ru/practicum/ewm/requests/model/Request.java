package ru.practicum.ewm.requests.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // Идентификатор заявки

    @Column(name = "requester_id")
    private long requesterId;       // Идентификатор пользователя, отправившего заявку

    @Column(name = "created_date")
    private LocalDateTime createdDate;  // Дата и время создания заявки

    @Column(name = "event_id")
    private long eventId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
