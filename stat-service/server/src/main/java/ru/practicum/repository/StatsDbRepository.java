package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsDbRepository extends JpaRepository<Event, Long> {
    @Query("")
    List<StatsDto> findAllStatistics(
            @Param("start")LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("")
    List<StatsDto> findUniqueStatistics(
            @Param("start")LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
