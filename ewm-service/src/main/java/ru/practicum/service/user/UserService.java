package ru.practicum.service.user;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.utility.crud.DefaultCreateService;
import ru.practicum.utility.crud.DefaultDeleteService;

import java.util.List;

public interface UserService extends DefaultCreateService<NewUserRequest, UserDto>, DefaultDeleteService<Long> {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
