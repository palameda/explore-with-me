package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.api.user.NewUserRequest;
import ru.practicum.api.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.utility.mapper.UserMapper;
import ru.practicum.utility.page.Page;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> idsList, Integer from, Integer size) {
        List<User> users = Optional.ofNullable(idsList)
                .map(ids -> userRepository.findAllByIdIn(ids, new Page(from, size, Sort.unsorted())))
                .orElse(userRepository.findAll(new Page(from, size, Sort.unsorted())).getContent());
        log.info("Получение списка пользователей размером {}", users.size());
        return List.copyOf(userMapper.toDto(users));
    }

    @Override
    public UserDto create(NewUserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .build();
        userRepository.save(user);
        log.info("Пользователь {} успешно создан", user.toString());
        return userMapper.toDto(user);
    }

    @Override
    public void delete(Long userId) {
        Optional.ofNullable(userId)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден в системе")))
                .ifPresent(userRepository::delete);
        log.info("Пользователь с id = {} успешно удалён", userId);
    }
}
