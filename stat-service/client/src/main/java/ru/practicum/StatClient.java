package ru.practicum;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

public interface StatClient {

    public void save(HitDto hitDto);

    public ResponseEntity<List<StatsDto>> getStats(String start, String end, List<String> uris, Boolean unique);

    public void changeUrl(String newUrl);
}
