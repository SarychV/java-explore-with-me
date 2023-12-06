package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.service.model.StatResult;

import java.util.List;

public interface StatsService {
    void saveStatisticEntry(StatEntryDto entry);

    List<StatResult> getStatisticByUris(String startDate, String endDate,
                                        List<String> uris, boolean unique);
}
