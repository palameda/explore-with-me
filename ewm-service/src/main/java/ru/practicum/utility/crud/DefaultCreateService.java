package ru.practicum.utility.crud;

public interface DefaultCreateService<CD, D> {
    D create(CD createDto);
}
