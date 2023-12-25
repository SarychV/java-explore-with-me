package ru.practicum.ewm.locations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.events.model.Event;

import javax.persistence.*;

import javax.persistence.GenerationType;
import java.util.List;

// Класс для описания зоны локаций для событий
@Setter
@Getter
@ToString
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // уникальный идентификатор локации

    private String name;            // название локации

    private String description;     // описание локации

    @Column(name = "longitude")
    private float lon;              // широта локации в градусах

    @Column(name = "latitude")
    private float lat;              // долгота локации в градусах

    private float radius;           // радиус зоны действия локации в километрах

    @OneToMany
    private List<Event> events;
}
