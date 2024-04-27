package ru.practicum.utility.crud.user;

public interface UserRequestUpdateService<D, ID, EID, R> {
    D update(ID userId, EID id, R request);
}
