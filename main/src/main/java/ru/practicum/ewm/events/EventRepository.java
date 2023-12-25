package ru.practicum.ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.users.model.User;

import java.time.LocalDateTime;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByInitiator(User user, Pageable page);

    List<Event> findByIdIn(List<Long> eventIds);

    @Query(value = "select e from Event as e " +
            "where " +
            ":userIds is null or e.initiator.id in :userIds " +
            "and :states is null or e.state in :states " +
            "and :categoryIds is null or e.category.id in :categoryIds " +
            "and e.eventDate between :startDate and :endDate")
    Page<Event> findEventsForAdmin(
            List<Long> userIds, List<EventState> states, List<Long> categoryIds,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query(value = "select e from Event as e " +
                    "where " +
                        ":text is null or lower(e.annotation) like concat('%', lower(:text), '%') " +
                            "or lower(e.description) like concat('%', lower(:text), '%') " +
                        "and :categoryIds is null or e.category.id in :categoryIds " +
                        "and :paid is null or e.paid = :paid " +
                        "and e.eventDate between :startDate and :endDate " +
                        "and e.state = :state " +
                        "and ((:onlyAvailable = true and (e.confirmedRequests < e.participantLimit " +
                                "or e.participantLimit = 0)) " +
                            "or :onlyAvailable = false)")
    Page<Event> findPublishedEventsByFilter(String text, List<Long> categoryIds, Boolean paid, EventState state,
                                            LocalDateTime startDate, LocalDateTime endDate,
                                            boolean onlyAvailable, Pageable page);

    @Query("select e from Event as e where distance(e.lat, e.lon, :latitude, :longitude) <= :radius")
    List<Event> findEventsInLocation(float latitude, float longitude, float radius);
}
