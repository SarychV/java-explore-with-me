package ru.practicum.ewm.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.exception.BadConditionException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoIn;
import ru.practicum.ewm.requests.dto.RequestsAndStatusDtoOut;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /*
    + нельзя добавить повторный запрос (Ожидается код ошибки 409)
    + инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
    + нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
    + если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
    - если для события отключена пре-модерация запросов на участие,
    то запрос должен автоматически перейти в состояние подтвержденного
    */
    @Override
    public RequestDto addRequest(long userId, long eventId){
        checkUser(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        // нельзя добавить повторный запрос
        Optional<Request> requestInRep = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requestInRep.isPresent()) {
            throw new DataIntegrityViolationException(
                    String.format("A request of userId=%d to eventId=%d has already been made before",
                            userId, eventId));
        }
        // инициатор события не может добавить запрос на участие в своем событии
        if (event.getInitiator().getId() == userId) {
            throw new DataIntegrityViolationException(
                    String.format("A userId=%d is an initiator of this eventId=%d",
                            userId, eventId));
        }
        // нельзя участвовать в неопубликованном событии
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataIntegrityViolationException(
                    String.format("An eventId=%d not published yet", eventId));
        }
        // достигнут лимит запросов на участие
        int limit = event.getParticipantLimit();
        int confirmedRequests = requestRepository.getHowManyRequestsHaveStatusForEvent(
                eventId, RequestStatus.CONFIRMED);
        if (limit > 0 && confirmedRequests >= limit) {
            throw new DataIntegrityViolationException(
                    String.format("An eventId=%d has the limit of participants=%d", eventId, limit));
        }

        Request request = new Request();
        if (event.isRequestModeration()) {
            // убрал [&& limit > 0) {], пока считаю что при limit=0 запросы должны иметь статус PENDING
            // т.к. при отсутствии ограничений по количеству, у инициатора могут быть другие причины
            // на отклонение заявки
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        request.setRequesterId(userId);
        request.setEventId(eventId);
        request.setCreatedDate(LocalDateTime.now());

        Request returnedRequest = requestRepository.save(request);
        RequestDto result = RequestMapper.toRequestDto(returnedRequest);
        log.info("To UsersRequestsController was returned requestDto={}", result);
        return result;
    }

    @Override
    public List<RequestDto> getUserRequests(long userId) {
        checkUser(userId);
        List<Request> userRequests = requestRepository.findByRequesterId(userId);
        List<RequestDto> result = RequestMapper.toRequestDtoList(userRequests);
        log.info("To the UsersRequestsController was returned: requestDtoList={}", result);
        return result;
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        checkUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("Request with id=%d was not found", requestId)));

        request.setStatus(RequestStatus.PENDING);
        Request returnedRequest = requestRepository.save(request);
        RequestDto result = RequestMapper.toRequestDto(returnedRequest);
        log.info("To the UsersRequestsController was returned: requestDto={}", result);
        return result;
    }

    @Override
    public List<RequestDto> giveUserTheRequestsForEvent(long userId, long eventId) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));
        List<Request> requests = new ArrayList<>();
        if (event.getInitiator().getId() != userId) {
            // если пользователь не инициатор события, то возвращаем направленный пользователем запрос
            // на участие в событии eventId
            Optional<Request> userRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
            userRequest.ifPresent(requests::add);
        } else {
            // если пользователь инициатор события, то возвращаем все запросы других пользователей
            // для события eventId
            requests = requestRepository.findByEventId(eventId);
        }

        List<RequestDto> result = RequestMapper.toRequestDtoList(requests);
        log.info("To the UsersRequestsController was returned: requestDtoList={}", result);
        return result;
    }

    /*
    - если для события [лимит заявок равен 0 или] отключена пре-модерация заявок,
     то подтверждение заявок не требуется
    + нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
    + статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
    + если при подтверждении данной заявки, лимит заявок для события исчерпан,
     то все неподтверждённые заявки необходимо отклонить
     */
    @Override
    @Transactional
    public RequestsAndStatusDtoOut changeRequestStatusForEvent(
            long userId, long eventId, RequestsAndStatusDtoIn requestsAndStatusDtoIn) {
        checkUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%d was not found", eventId)));
        // пользователь должен быть инициатором заявки, чтобы запрос обработался
        if (event.getInitiator().getId() != userId) {
            throw new DataIntegrityViolationException(
                    String.format("A userId=%d is not an initiator of this eventId=%d", userId, eventId));
        }

        int limit = event.getParticipantLimit();
        int confirmedRequests = requestRepository.getHowManyRequestsHaveStatusForEvent(
                eventId, RequestStatus.CONFIRMED);
        // достигнут лимит по заявкам на данное событие
        if (limit > 0 && confirmedRequests >= limit) {
            throw new BadConditionException("The participant limit has been reached");
        }
        int mayConfirmed = limit - confirmedRequests;

        RequestsAndStatusDtoOut result = new RequestsAndStatusDtoOut();
        List<RequestDto> confirmedRequestDto = result.getConfirmedRequests();
        List<RequestDto> rejectedRequestDto = result.getRejectedRequests();
        List<Request> processedRequests = requestRepository.findByIdInOrderByCreatedDate(
                requestsAndStatusDtoIn.getRequestIds());
        RequestStatus newStatus = requestsAndStatusDtoIn.getStatus();

        for (Request request : processedRequests) {
            if (request != null) {
                Request returnedRequest;
                if (request.getStatus() != RequestStatus.PENDING) {
                    throw new BadConditionException("The request has the bad status (not 'PENDING')");
                }

                if (mayConfirmed > 0) {
                    request.setStatus(newStatus);
                    returnedRequest = requestRepository.save(request);
                    if (newStatus == RequestStatus.CONFIRMED) {
                        confirmedRequestDto.add(RequestMapper.toRequestDto(returnedRequest));
                    } else if (newStatus == RequestStatus.REJECTED) {
                        rejectedRequestDto.add(RequestMapper.toRequestDto(returnedRequest));
                    }
                    mayConfirmed = mayConfirmed - 1;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    returnedRequest = requestRepository.save(request);
                    rejectedRequestDto.add(RequestMapper.toRequestDto(returnedRequest));
                }
            }
        }
        return result;
    }

    private void checkUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
    }
}
