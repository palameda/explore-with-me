package ru.practicum.model;

import java.util.Optional;

public enum EventSort {
    EVENT_DATE,
    VIEWS;

    public static Optional<EventSort> from(String stringSort) {
        for (EventSort sort : EventSort.values()) {
            if (sort.name().equalsIgnoreCase(stringSort)) {
                return Optional.of(sort);
            }
        }
        return Optional.empty();
    }
}
