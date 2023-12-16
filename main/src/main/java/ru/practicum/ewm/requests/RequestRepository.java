package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    // поиск запроса по идентификатору пользователя и идентификатору события
    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    @Query("select count(r.eventId) from Request as r where r.eventId = ?1 and r.status = ?2")
    int getHowManyRequestsHaveStatusForEvent(long eventId, RequestStatus status);

    List<Request> findByRequesterId(long requesterId);

    List<Request> findByEventId(long eventId);

    List<Request> findByIdInOrderByCreatedDate(List<Long> requestIds);

    // Получить информацию по количеству подтвержденных запросов в отношении каждого id
    @Query("select r.eventId, count(r.id) " +
            "from Request as r " +
            "where r.eventId in ?1 and r.status=?2 " +
            "group by r.eventId")
    Map<Long, Integer> getConfirmedRequestCountsByEventIds(List<Long> eventsIds, RequestStatus status);
}
