package ru.practicum.ewm.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.Constant;
import ru.practicum.ewm.stats.client.BadHttpResponseException;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.dto.StatResultDto;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class StatisticService {
    private final RestTemplateBuilder builder;
    private final String serverUrl;
    private final StatsClient statsClient;

    public StatisticService(RestTemplateBuilder restBuilder, @Value("${stats-server.url}") String url) {
        this.builder = restBuilder;
        this.serverUrl = url;
        this.statsClient = new StatsClient(serverUrl, builder);
    }

    public void sendStatistics(String ip, String uri) {
        try {
            StatEntryDto statEntryDto = new StatEntryDto();
            statEntryDto.setApp(Constant.SERVICE_NAME);
            statEntryDto.setTimestamp(Constant.DATE_TIME_WHITESPACE.format(LocalDateTime.now()));
            statEntryDto.setIp(ip);
            statEntryDto.setUri(uri);
            statsClient.sendStatistics(statEntryDto);
        } catch (BadHttpResponseException e) {
            e.printStackTrace();
            log.warn("Main server has received bad HTTP-response from statistics server.");
        }
    }

    // Получить статистику просмотров событий
    // Long - идентификатор события (id), Long - количество просмотров события (views)
    public Map<Long, Long> receiveStatisticsByEventIds(List<Long> eventIds, boolean unique) {
        Map<Long, Long> result = new HashMap<>();
        Map<Long, Long> gottenIdViews;
        Optional<List<StatResultDto>> statistics = Optional.empty();

        if (eventIds.size() == 0) {
            return result;
        }
        try {
            statistics = statsClient.getStatistics(
                    Constant.DATE_MIN,                  // startDate
                    LocalDateTime.now(),                // endDate
                    makeUrisFromEventIds(eventIds),     // uris
                    unique);                            // by unique IP address or not
        } catch (BadHttpResponseException e) {
            e.printStackTrace();
            log.warn("EWM Main server has received bad HTTP-response from statistics server.");
        }

        if (statistics.isPresent()) {
            gottenIdViews = convertStatisticsToMap(statistics.get()); // сведения есть
        } else {
            gottenIdViews = new HashMap<>();
        }

        // Подготовить ответ для всего перечня eventIds, учитывая возможность отсутствия
        // информации для конкретных id (в базе статистки id не встречались). В этом случае проставляются нули.
        for (Long id : eventIds) {
            Long views = gottenIdViews.get(id);
            if (views != null) {
                result.put(id, views);
            } else {
                result.put(id, 0L);
            }
        }
        return result;
    }

    private List<String> makeUrisFromEventIds(List<Long> eventIds) {
        List<String> uris = new ArrayList<>();
        for (Long id : eventIds) {
            if (id != null) {
                uris.add("/events/" + id);
            }
        }
        return uris;
    }

    private Map<Long, Long> convertStatisticsToMap(List<StatResultDto> stats) {
        Map<Long, Long> result = new HashMap<>();

        for (StatResultDto record: stats) {
            if (record.getApp().equals(Constant.SERVICE_NAME)) {
                Long key = null;
                Long value;
                String uri = record.getUri();
                String[] uriElements = uri.split("/");
                if (uriElements[0].isEmpty() && uriElements[1].equals("events")) {
                    key = Long.valueOf(uriElements[2]);
                }
                value = (long) record.getHits();
                if (key != null) result.put(key, value);
            }
        }
        return result;
    }
}
