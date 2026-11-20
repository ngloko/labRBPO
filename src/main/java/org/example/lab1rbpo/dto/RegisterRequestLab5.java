package org.example.lab1rbpo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestLab5(
        @NotBlank @Size(max = 64) String username,
        @NotBlank String password,
        @NotBlank @Size(max = 150) String name,
        String deviceId
) {}