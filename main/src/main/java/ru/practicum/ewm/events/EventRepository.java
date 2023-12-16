package ru.practicum.ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.users.model.User;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiator(User user, Pageable page);

    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
            List<Long> userIds, List<EventState> states, List<Long> categoryIds,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query(value =
           /* //"select e from (" +
            "select e, count(r.status) from Event as e left join Request as r on e.id = r.eventId " +
            "where " +
                    ":text is null or lower(e.annotation) like concat('%', lower(:text), '%') " +
                    "or lower(e.description) like concat('%', lower(:text), '%') " +
                "and :categoryIds is null or e.category.id in :categoryIds " +
                "and :paid is null or e.paid = :paid " +
                "and e.state = :state " +
            "group by e.id " +
            "having (:onlyAvailable = true and (count(r.eventId) < e.participantLimit or e.participantLimit = 0)) " +
                   "or :onlyAvailable = false "*/

            /*"select e from Event as e " +
                    "where " +
                    ":text is null or lower(e.annotation) like concat('%', lower(:text), '%') " +
                    "or lower(e.description) like concat('%', lower(:text), '%') " +
                    "and :categoryIds is null or e.category.id in :categoryIds " +
                    "and :paid is null or e.paid = :paid " +
                    "and e.eventDate between :startDate and :endDate " +
                    "and e.state = :state " +*/

            "select e from ( " +
                    "select e from Event as e " +
                    "where " +
                    ":text is null or lower(e.annotation) like concat('%', lower(:text), '%') " +
                        "or lower(e.description) like concat('%', lower(:text), '%') " +
                    "and :categoryIds is null or e.category.id in :categoryIds " +
                    "and :paid is null or e.paid = :paid " +
                    "and e.eventDate between :startDate and :endDate " +
                    "and e.state = :state ) " +
                    "group by e.id " +
                    "having (:onlyAvailable = true and (count(r.eventId) < e.participantLimit or e.participantLimit = 0)) " +
                        "or :onlyAvailable = false "
    )
    List<Event> find(String text, List<Long> categoryIds, Boolean paid,
                     EventState state, LocalDateTime startDate, LocalDateTime endDate, boolean onlyAvailable);
}
