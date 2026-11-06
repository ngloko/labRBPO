package org.example.lab1rbpo.dto;

/**
 * Создание/изменение размещения экспоната (CRUD-уровень).
 * Поля необязательны для PATCH; обязательность проверяется в сервисе при создании.
 */
public record PlacementRequest(
        Long exhibitId,

        Long exhibitionId,

        Long hallId,

        String status
) {
}
