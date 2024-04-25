package ru.practicum.utility.crud.user;

public interface UserRequestCreateService<D, ID, C> {
    D create(ID userRequestFromId, C c);
}
