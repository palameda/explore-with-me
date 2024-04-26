package ru.practicum.utility.crud.user;

import java.util.Collection;

public interface UserRequestGetAllService<D, ID> {
    Collection<D> getAllByUser(ID id, Integer from, Integer size);
}
