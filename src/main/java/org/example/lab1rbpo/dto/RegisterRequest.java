package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Самостоятельная регистрация пользователя.
 * Роль здесь намеренно отсутствует: регистрирующийся всегда получает CURATOR,
 * чтобы нельзя было самому назначить себе повышенные привилегии.
 */
public record RegisterRequest(
        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 64, message = "Логин должен содержать от 3 до 64 символов")
        String username,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 150, message = "Имя не длиннее 150 символов")
        String name,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 72, message = "Пароль должен содержать от 8 до 72 символов")
        @Pattern(
                regexp = "^(?=.*[A-Za-zА-Яа-яЁё])(?=.*\\d).+$",
                message = "Пароль должен содержать и буквы, и цифры"
        )
        String password
) {
}