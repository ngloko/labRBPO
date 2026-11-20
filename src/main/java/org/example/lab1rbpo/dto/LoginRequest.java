package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password,
        String deviceId
) {}