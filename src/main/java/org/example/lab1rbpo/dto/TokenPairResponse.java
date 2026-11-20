package org.example.lab1rbpo.dto;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {}