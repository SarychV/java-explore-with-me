package ru.practicum.ewm.compilations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // Уникальный идентификатор подборки

    private String title;           // Заголовок подборки

    private boolean pinned;         // Закреплена ли подборка на главной странице сайта

    @Column()
    @OneToMany
    private List<Event> events;     // Перечень событий в подборке
}
