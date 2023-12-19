package ru.practicum.ewm.requests.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.requests.model.RequestStatus;

import java.util.List;

@ToString
@Setter
@Getter
public class RequestsAndStatusDtoIn {
    private List<Long> requestIds;
    private RequestStatus status;
}
