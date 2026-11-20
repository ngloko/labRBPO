package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.LoginRequest;
import org.example.lab1rbpo.dto.RegisterRequestLab5;
import org.example.lab1rbpo.dto.TokenPairResponse;
import org.example.lab1rbpo.entity.Role;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.exception.InvalidCredentialsException;
import org.example.lab1rbpo.exception.UsernameAlreadyExistsException;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenPairService tokenPairService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenPairService tokenPairService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPairService = tokenPairService;
    }

    public TokenPairResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(User::isEnabled)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new InvalidCredentialsException("Неверный логин или пароль"));

        return tokenPairService.issuePair(user, request.deviceId());
    }

    @Transactional
    public TokenPairResponse register(RegisterRequestLab5 request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setName(request.name());
        user.setRole(Role.CURATOR); // роль фиксирована: повышенную нельзя получить самостоятельно
        user.setEnabled(true);
        userRepository.save(user);

        return tokenPairService.issuePair(user, request.deviceId());
    }
}