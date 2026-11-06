package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Бизнес-операция «разместить экспонат на выставке в зале».
 */
public record PlaceExhibitRequest(
        @NotNull(message = "exhibitionId обязателен")
        Long exhibitionId,

        @NotNull(message = "exhibitId обязателен")
        Long exhibitId,

        @NotNull(message = "hallId обязателен")
        Long hallId
) {
}
