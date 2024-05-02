package ru.practicum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

public class StatClientImpl implements StatClient {
    private WebClient webClient;

    public StatClientImpl(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    @Override
    public void save(HitDto hitDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(hitDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
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

    @Override
    public void changeUrl(String newUrl) {
        this.webClient = WebClient.create(newUrl);
    }
}
