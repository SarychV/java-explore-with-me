package ru.practicum.ewm.users.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserShortDto {
    private long id;

    private String name;
}
