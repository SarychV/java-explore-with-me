package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.map.DateMapper;
import ru.practicum.ewm.stats.service.model.StatEntry;
import ru.practicum.ewm.stats.dto.StatEntryDto;

public class StatsMapper {
    public static StatEntry toStatEntry(StatEntryDto entryDto) {
        StatEntry result = new StatEntry();
        result.setApp(entryDto.getApp());
        result.setUri(entryDto.getUri());
        result.setIp(entryDto.getIp());
        result.setQueryDate(DateMapper.toLocalDateTime(entryDto.getTimestamp()));
        return result;
    }
}
