package ru.practicum.ewm.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.RequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class UsersRequestsController {
    private final RequestService requestService;

    public UsersRequestsController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(
            @PathVariable @Positive long userId,
            @RequestParam @PositiveOrZero long eventId) {
        log.info("requestService.addRequest() was invoked with arguments userId={}, eventId={}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping()
    public List<RequestDto> getCurrentUserRequests(
            @PathVariable @Positive long userId) {
        log.info("requestService.getUserRequests() was invoked with arguments userId={}", userId);
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("{requestId}/cancel")
    public RequestDto cancelRequest(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long requestId) {
        log.info("requestService.cancelRequest() was invoked with arguments userId={}, requestId={}",
                userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
