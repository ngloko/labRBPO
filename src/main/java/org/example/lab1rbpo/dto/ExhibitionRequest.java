package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Создание/изменение выставки. Куратор задаётся идентификатором пользователя.
 * Поля необязательны для PATCH; обязательность проверяется в сервисе при создании.
 */
public record ExhibitionRequest(
        @Size(max = 150, message = "Название выставки не длиннее 150 символов")
        String name,

        LocalDateTime startDate,

        LocalDateTime endDate,

        String status,

        Long curatorId
) {
}
