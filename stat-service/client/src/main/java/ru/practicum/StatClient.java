package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

public class StatClient {
    private WebClient webClient;

    public StatClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public void save(EventDto eventDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(eventDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<List<StatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("uris", uris)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("unique", unique)
                        .build()
                )
                .retrieve()
                .toEntityList(StatsDto.class)
                .block();
    }

    public void changeUrl(String newUrl) {
        this.webClient = WebClient.create(newUrl);
    }
}
