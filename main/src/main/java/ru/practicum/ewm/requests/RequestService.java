package ru.practicum.ewm.requests;

import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoIn;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoOut;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(long userId, long eventId);

    List<RequestDto> getUserRequests(long userId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> giveUserEventRequests(long userId, long eventId);

    RequestsAndStatusDtoOut changeEventRequestStatus(long userId, long eventId,
                                                        RequestsAndStatusDtoIn requestsAndStatusDtoIn);
}

