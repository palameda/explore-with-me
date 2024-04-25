package ru.practicum.utility.crud;

public interface DefaultCreateService<CMD, D> {
    D create(CMD createDto);
}
