package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.Size;

/**
 * Создание/изменение пользователя. При создании обязательны username, name, role, password.
 */
public record UserRequest(
        @Size(min = 3, max = 64, message = "Логин должен содержать от 3 до 64 символов")
        String username,

        @Size(max = 150, message = "Имя не длиннее 150 символов")
        String name,

        String role,

        @Size(min = 6, max = 72, message = "Пароль должен содержать от 6 до 72 символов")
        String password
) {
}
