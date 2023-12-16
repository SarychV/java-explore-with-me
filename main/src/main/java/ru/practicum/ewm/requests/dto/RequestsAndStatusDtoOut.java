package ru.practicum.ewm.requests.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RequestsAndStatusDtoOut {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
