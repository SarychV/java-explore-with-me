package ru.practicum.ewm.events;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@AutoConfigureDataJpa
class EventServiceImplTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testQuery() {
        Pageable page = PageRequest.of(0, 1);
        User user = userRepository.findById(1L).get();
        Page<Event> eventPage = eventRepository.findAllByInitiator(user, page);
        List<Event> events = eventPage.stream().collect(Collectors.toList());
        log.info("List of events = {}", events);
    }

    @Test
    void testConfirmedEventCounts() {
        //List<Event> result = eventRepository.find("harum", null, null,
        //        EventState.PUBLISHED, false);
        //log.info("Список событий: result={}", result);
    }
}