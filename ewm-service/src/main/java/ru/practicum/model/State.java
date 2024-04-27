package ru.practicum.model;

import java.util.Optional;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static Optional<State> getFrom(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
