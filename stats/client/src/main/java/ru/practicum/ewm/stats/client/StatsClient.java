package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.dto.StatResultDto;
import ru.practicum.ewm.stats.map.DateMapper;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class StatsClient {
    protected final RestTemplate rest;

    public StatsClient(String serverUrl, RestTemplateBuilder builder) {
        rest = builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    public void sendStatistics(StatEntryDto statEntryDto) throws BadHttpResponseException {
            post("/hit", statEntryDto);
    }

    public Optional<List<StatResultDto>> getStatistics(LocalDateTime start, LocalDateTime end,
                    List<String> uris, Boolean unique) throws BadHttpResponseException {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder path = new StringBuilder("/stats?start={start}&end={end}");

        if (start == null) {
            start = LocalDateTime.MIN;
        }
        parameters.put("start", DateMapper.localDateTimeToStringWithEncoding(start));

        if (end == null) {
            end = LocalDateTime.MAX;
        }
        parameters.put("end", DateMapper.localDateTimeToStringWithEncoding(end));

        if (uris != null) {
            path.append("&uris={uris}");
            String urisPathString = makeUrisPathString(uris);
            parameters.put("uris", urisPathString);
        }

        if (unique) {
            path.append("&unique={unique}");
            parameters.put("unique", unique.toString());
        }

        ResponseEntity<List<StatResultDto>> response = get(path.toString(), parameters);

        List<StatResultDto> result = response.getBody();
        if (result != null) {
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    protected <T> ResponseEntity<Object> post(String path, T body)
            throws BadHttpResponseException {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            throw new BadHttpResponseException();
        }
        return statsServerResponse;
    }

    protected ResponseEntity<List<StatResultDto>> get(String path, Map<String, Object> parameters)
            throws BadHttpResponseException {
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<List<StatResultDto>> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity,
                    new ParameterizedTypeReference<List<StatResultDto>>() {},
                    parameters);
        } catch (HttpStatusCodeException e) {
            throw new BadHttpResponseException();
        }
        return statsServerResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private String makeUrisPathString(List<String> uris) {
        StringBuilder urisStr = new StringBuilder();
        for (String uri : uris) {
            urisStr.append(uri).append(',');
        }
        urisStr.deleteCharAt(urisStr.length() - 1);
        return urisStr.toString();
    }
}
