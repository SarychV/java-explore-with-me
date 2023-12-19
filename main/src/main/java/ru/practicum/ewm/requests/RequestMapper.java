package ru.practicum.ewm.requests;

import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        RequestDto result = new RequestDto();
        result.setId(request.getId());
        result.setRequester(request.getRequesterId());
        result.setEvent(request.getEventId());
        result.setStatus(request.getStatus().name());
        result.setCreated(request.getCreatedDate().toString());
        return result;
    }

    public static List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
