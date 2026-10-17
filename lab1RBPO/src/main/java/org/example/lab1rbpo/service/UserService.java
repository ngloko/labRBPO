package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAll() { return repository.findAll(); }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User create(User user) { return repository.save(user); }

    public User update(Long id, User user) {
        User existing = getById(id);
        existing.setName(user.getName());
        return repository.save(existing);
    }

    public void delete(Long id) {
        getById(id); // проверка на существование
        repository.deleteById(id);
    }
}