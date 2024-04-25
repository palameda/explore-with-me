package ru.practicum.utility.crud;

import java.util.Collection;

public interface DefaultGetService<ID, D> {
    Collection<D> getAll(Integer from, Integer size);
    D get(ID id);
}
