package ru.practicum.utility.crud;

public interface DefaultUpdateService<U, D> {
    D update(Long id, U update);
}
