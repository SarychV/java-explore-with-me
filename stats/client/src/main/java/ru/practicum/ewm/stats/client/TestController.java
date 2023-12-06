package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.StatResultDto;
import ru.practicum.ewm.stats.map.DateMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@ControllerAdvice
public class TestController {
    private final RestTemplateBuilder builder;
    private final String serverUrl;

    @Autowired
    public TestController(RestTemplateBuilder restBuilder,
                           @Value("${stats-server.url}") String url) {
        this.builder = restBuilder;
        this.serverUrl = url;
    }

    @GetMapping("/test")
    public @ResponseBody List<StatResultDto> getStatisticsByUrisOverClient(
            @RequestParam(value = "start", required = true) String start,
            @RequestParam(value = "end", required = true) String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique)
            throws BadHttpResponseException {

        log.info("На эндпойнте '/test' получен GET-запрос с параметрами startDate={}, endDate={}, uris={}, unique={}",
                start, end, uris, unique);

        LocalDateTime startDateTime = DateMapper.toLocalDateTime(start);
        LocalDateTime endDateTime = DateMapper.toLocalDateTime(end);
        Optional<List<StatResultDto>> result = new StatsClient(serverUrl, builder).getStatistics(
                startDateTime, endDateTime, uris, unique);
        if (result.isEmpty()) {
            log.info("Сервер статистики не сформировал корректный список сведений.");
            return null;
        } else {
            List<StatResultDto> statisticsResult = result.get();
            log.info("По запросу получена информация List<StatResultDto>: {}", statisticsResult);
            return statisticsResult;
        }
    }
}