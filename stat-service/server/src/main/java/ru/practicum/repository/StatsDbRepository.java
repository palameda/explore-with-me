package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsDbRepository extends JpaRepository<Event, Long> {
    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, count(e.id)) " +
            "FROM Event as e " +
            "WHERE (e.uri IN :uris) " +
            "AND (e.created BETWEEN :start AND :end) " +
            "GROUP BY e.uri, e.app " +
            "ORDER BY count(e.id) DESC")
    List<StatsDto> findRequiredStatistics(
            @Param("start")LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, count(DISTINCT e.id)) " +
            "FROM Event as e " +
            "WHERE (e.uri IN :uris) " +
            "AND (e.created BETWEEN :start AND :end) " +
            "GROUP BY e.uri, e.app " +
            "ORDER BY count(e.id) DESC")
    List<StatsDto> findUniqueStatistics(
            @Param("start")LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
