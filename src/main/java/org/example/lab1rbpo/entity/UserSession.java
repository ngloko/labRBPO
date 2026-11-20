package org.example.lab1rbpo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Запись о выданной паре access/refresh токенов (таблица user_sessions).
 * Одна запись = одна пара. После обновления старая запись сохраняется
 * со статусом USED, для новой пары создаётся новая строка.
 */
@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 64)
    private String username;

    @Column(name = "device_id")
    private String deviceId;

    @NotBlank
    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @NotBlank
    @Column(name = "refresh_token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String refreshToken;

    @NotNull
    @Column(name = "access_token_expiry", nullable = false)
    private Instant accessTokenExpiry;

    @NotNull
    @Column(name = "refresh_token_expiry", nullable = false)
    private Instant refreshTokenExpiry;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;
}