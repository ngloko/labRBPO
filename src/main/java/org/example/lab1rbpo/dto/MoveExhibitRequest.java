package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Бизнес-операция «переместить экспонат в другой зал».
 */
public record MoveExhibitRequest(
        @NotNull(message = "hallId обязателен")
        Long hallId
) {
}
