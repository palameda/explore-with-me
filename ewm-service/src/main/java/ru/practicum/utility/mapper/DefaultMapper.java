package ru.practicum.utility.mapper;

import java.util.Collection;
import java.util.stream.Collectors;

public interface DefaultMapper<E, D> {
    D toDto(E e);

    default Collection<D> toDto(Collection<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
