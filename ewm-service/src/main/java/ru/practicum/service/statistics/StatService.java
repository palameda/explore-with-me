package ru.practicum.service.statistics;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ClientFactory;
import ru.practicum.StatClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StatService {
    private static StatClient client;
    private final HttpServletRequest httpRequest;
    @Value("${stat.client.baseurl}")
    private String url;
    private final DateTimeFormatter formatter;

    @PostConstruct
    void init() {
        log.info("baseurl = {}", url);
        client = ClientFactory.getDefaultClient(url);
    }

    @Pointcut("@annotation(Statistics)")
    void savingProcess() {
    }

    @After("savingProcess()")
    @SneakyThrows
    void doSaving() {
        log.info(String.format("Сохраняем в статистику: %s %s", httpRequest.getMethod(), httpRequest.getRequestURI()));
        client.save(generateEndpointHit());
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return client.getStats(formatter.format(start), formatter.format(end), uris, unique).getBody();
    }

    private HitDto generateEndpointHit() {
        return new HitDto("main-service", httpRequest.getRequestURI(), httpRequest.getRemoteAddr(), LocalDateTime.now());
    }
}
