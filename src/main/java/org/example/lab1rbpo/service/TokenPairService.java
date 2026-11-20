package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.TokenPairResponse;
import org.example.lab1rbpo.entity.SessionStatus;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.entity.UserSession;
import org.example.lab1rbpo.exception.InvalidRefreshTokenException;
import org.example.lab1rbpo.repo.UserRepository;
import org.example.lab1rbpo.repo.UserSessionRepository;
import org.example.lab1rbpo.security.InvalidTokenException;
import org.example.lab1rbpo.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenPairService {

    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public TokenPairService(UserSessionRepository sessionRepository,
                            UserRepository userRepository,
                            JwtService jwtService) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /** Выдача пары после успешного входа либо создания учётной записи. */
    @Transactional
    public TokenPairResponse issuePair(User user, String deviceId) {
        JwtService.GeneratedToken access = jwtService.generateAccessToken(user.getUsername());
        JwtService.GeneratedToken refresh = jwtService.generateRefreshToken(user.getUsername());

        UserSession session = UserSession.builder()
                .username(user.getUsername())
                .deviceId(deviceId)
                .accessToken(access.token())
                .refreshToken(refresh.token())
                .accessTokenExpiry(access.expiry())
                .refreshTokenExpiry(refresh.expiry())
                .status(SessionStatus.ACTIVE)
                .build();

        sessionRepository.save(session);

        return new TokenPairResponse(access.token(), refresh.token());
    }

    /**
     * Обмен действующего refresh на новую пару.
     * noRollbackFor — чтобы перевод "засвеченного" refresh в REVOKED
     * (см. ниже) сохранился, даже если сам метод завершается отказом.
     */
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public TokenPairResponse refreshPair(String refreshToken) {
        var claims = validateRefreshJwt(refreshToken);
        String username = claims.getSubject();

        UserSession session = sessionRepository.findByRefreshTokenForUpdate(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Неизвестная refresh-сессия"));

        if (!session.getUsername().equals(username)) {
            throw new InvalidRefreshTokenException("Токен не соответствует сессии");
        }

        Instant now = Instant.now();
        boolean expired = !session.getRefreshTokenExpiry().isAfter(now);

        if (session.getStatus() == SessionStatus.USED && !expired) {
            // Повторное предъявление уже использованного, но ещё не истёкшего
            // refresh — признак компрометации. Отзываем сессию и возвращаем отказ.
            session.setStatus(SessionStatus.REVOKED);
            sessionRepository.save(session);
            throw new InvalidRefreshTokenException("Refresh уже был использован, сессия отозвана");
        }

        if (session.getStatus() != SessionStatus.ACTIVE || expired) {
            throw new InvalidRefreshTokenException("Refresh-сессия недействительна");
        }

        User user = userRepository.findByUsername(username)
                .filter(User::isEnabled)
                .orElseThrow(() -> new InvalidRefreshTokenException("Пользователь недоступен"));

        session.setStatus(SessionStatus.USED);
        sessionRepository.save(session);

        return issuePair(user, session.getDeviceId());
    }

    private io.jsonwebtoken.Claims validateRefreshJwt(String refreshToken) {
        try {
            return jwtService.validate(refreshToken, JwtService.TYPE_REFRESH);
        } catch (InvalidTokenException e) {
            throw new InvalidRefreshTokenException("Невалидный, просроченный или неподходящий по типу refresh-токен");
        }
    }
}