package ru.practicum.ewm.admin.users.model;


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
    private Long id;        // уникальный идентификатор пользователя
    private String name;    // уникальное имя пользователя
    private String email;   // уникальный адрес электронной почты пользователя
}
