package org.example.lab1rbpo.repo;

import jakarta.persistence.LockModeType;
import org.example.lab1rbpo.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    /**
     * Блокирующее чтение (SELECT ... FOR UPDATE). Нужно, чтобы при двух
     * одновременных запросах с одним refresh второй дождался коммита первого
     * и увидел уже обновлённый статус (USED), а не исходный (ACTIVE).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from UserSession s where s.refreshToken = :refreshToken")
    Optional<UserSession> findByRefreshTokenForUpdate(@Param("refreshToken") String refreshToken);
}