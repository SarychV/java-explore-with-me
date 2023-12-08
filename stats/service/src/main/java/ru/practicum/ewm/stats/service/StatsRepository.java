package ru.practicum.ewm.stats.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.service.model.StatEntry;
import ru.practicum.ewm.stats.service.model.StatResult;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatEntry, Long> {
    @Query("select new ru.practicum.ewm.stats.service.model.StatResult(se.app, se.uri, count(se.uri)) " +
            "from StatEntry se " +
            "where se.queryDate > ?1 and se.queryDate < ?2 and se.uri in ?3 " +
            "group by se.app, se.uri " +
            "order by count(se.uri) desc")
    List<StatResult> findByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.stats.service.model.StatResult(se.app, se.uri, count(se.uri)) " +
            "from StatEntry se " +
            "where se.queryDate > ?1 and se.queryDate < ?2 " +
            "group by se.app, se.uri " +
            "order by count(se.uri) desc")
    List<StatResult> findByAllUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.stats.service.model.StatResult(se.app, se.uri, count(distinct se.ip)) " +
            "from StatEntry se " +
            "where se.queryDate > ?1 and se.queryDate < ?2 and se.uri in ?3 " +
            "group by se.app, se.uri " +
            "order by count(se.uri) desc")
    List<StatResult> findByUrisAndUniqueIP(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.stats.service.model.StatResult(se.app, se.uri, count(distinct se.ip)) " +
            "from StatEntry se " +
            "where se.queryDate > ?1 and se.queryDate < ?2 " +
            "group by se.app, se.uri " +
            "order by count(se.uri) desc")
    List<StatResult> findByAllUrisAndUniqueIP(LocalDateTime start, LocalDateTime end);
}