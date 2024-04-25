package ru.practicum.utility.crud.user;

import java.util.Collection;

public interface UserRequestGetService<D, ID, EID> {
    Collection<D> getAllByUser(ID id, Integer from, Integer size);

    D get(ID userId, EID id);
}
