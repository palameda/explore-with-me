package ru.practicum.api.user;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
