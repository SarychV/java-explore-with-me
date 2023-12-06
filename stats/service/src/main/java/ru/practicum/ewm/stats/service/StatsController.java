package ru.practicum.ewm.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.service.model.StatResult;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


@RestController
@Slf4j
public class StatsController {
    private final StatsService statsService;


    @Autowired
    public StatsController(StatsService service) {
        this.statsService = service;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStatisticEntry(@RequestBody @Valid StatEntryDto statEntryDto) {
        log.info("Получен POST-запрос с параметрами statEntryDto={}", statEntryDto);
        statsService.saveStatisticEntry(statEntryDto);
    }

    @GetMapping("/stats")
    public @ResponseBody List<StatResult> getStatisticsByUris(
            @RequestParam(value = "start", required = true) String start,
            @RequestParam(value = "end", required = true) String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique)
            throws UnsupportedEncodingException {

        String startDate = URLDecoder.decode(start, "UTF-8");
        String endDate = URLDecoder.decode(end, "UTF-8");
        log.info("На эндпойнте '/stats' получен GET-запрос с параметрами startDate={}, endDate={}, uris={}, unique={}",
                start, end, uris, unique);
        return statsService.getStatisticByUris(startDate, endDate, uris, unique);
    }
}
