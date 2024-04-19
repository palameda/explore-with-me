package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsDbRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Hit e " +
            "WHERE e.created BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<StatsDto> findAllStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Hit e " +
            "WHERE e.created BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<StatsDto> findStatisticsByUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Hit e " +
            "WHERE e.created BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<StatsDto> findStatisticsByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Hit e " +
            "WHERE e.created BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<StatsDto> findStatisticsByUrisByUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                                  @Param("uris") List<String> uris);
}
