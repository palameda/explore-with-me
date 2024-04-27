package ru.practicum.utility.crud.user;

public interface UserRequestGetService<D, ID, EID> {

    D get(ID userId, EID id);
}
