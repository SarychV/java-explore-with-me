package ru.practicum.ewm.requests.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestDto {
    private String created;     // Дата и время создания заявки в формате "yyyy-MM-dd HH:mm:ss"

    private long event;         // Идентификатор события

    private long id;            // Идентификатор заявки

    private long requester;     // Идентификатор пользователя, отправившего заявку

    private String status;      // Статус заявки
}
