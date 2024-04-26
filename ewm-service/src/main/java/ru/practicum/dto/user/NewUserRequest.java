package ru.practicum.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewUserRequest {
    @Size(min = 6, max = 254, message = "Поле email должно быть не короче 6 и не длиннее 254 символов")
    @Email
    @NotBlank
    private String email;
    @Size(min = 2, max = 250, message = "Поле name должно быть не короче 2 и не длиннее 250 символов")
    @NotBlank
    private String name;
}
