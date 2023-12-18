package ru.practicum.ewm.users.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // Уникальный идентификатор пользователя
    private String name;    // Уникальное имя пользователя
    private String email;   // Уникальный адрес электронной почты пользователя
}
