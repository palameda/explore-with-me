package ru.practicum.utility.crud.user;

import java.util.Collection;

public interface UserRequestGetService<D, ID, EID> {

    D get(ID userId, EID id);
}
