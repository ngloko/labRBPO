package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.RegisterRequest;
import org.example.lab1rbpo.dto.UserRequest;
import org.example.lab1rbpo.entity.Role;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitionRepository;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Пользователи приложения (кураторы и администраторы).
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final ExhibitionRepository exhibitionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       ExhibitionRepository exhibitionRepository,
                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.exhibitionRepository = exhibitionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));
    }

    @Transactional
    public User create(UserRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new BusinessRuleException("Логин обязателен");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessRuleException("Имя обязательно");
        }
        if (request.password() == null || request.password().length() < 6) {
            throw new BusinessRuleException("Пароль должен содержать не менее 6 символов");
        }
        Role role = parseRole(request.role());
        if (repository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Пользователь с логином '" + request.username() + "' уже существует");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setRole(role);
        user.setEnabled(true);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return repository.save(user);
    }

    /**
     * Самостоятельная регистрация. В отличие от create(), роль всегда CURATOR
     * и не может быть передана клиентом — так исключается самоповышение привилегий.
     */
    @Transactional
    public User register(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Пользователь с логином '" + request.username() + "' уже существует");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setRole(Role.CURATOR);
        user.setEnabled(true);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        return repository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest request) {
        User existing = getById(id);

        if (request.username() != null && !request.username().isBlank()
                && !request.username().equals(existing.getUsername())) {
            if (repository.existsByUsername(request.username())) {
                throw new BusinessRuleException("Пользователь с логином '" + request.username() + "' уже существует");
            }
            existing.setUsername(request.username());
        }
        if (request.name() != null && !request.name().isBlank()) {
            existing.setName(request.name());
        }
        if (request.role() != null && !request.role().isBlank()) {
            existing.setRole(parseRole(request.role()));
        }
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 6) {
                throw new BusinessRuleException("Пароль должен содержать не менее 6 символов");
            }
            existing.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User user = getById(id);
        if (exhibitionRepository.existsByCurator_Id(id)) {
            throw new BusinessRuleException("Нельзя удалить пользователя: он назначен куратором выставок");
        }
        repository.delete(user);
    }

    private static Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            throw new BusinessRuleException("Роль обязательна (ADMIN или CURATOR)");
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Неизвестная роль: " + role + ". Допустимы ADMIN и CURATOR");
        }
    }
}