package org.example.lab1rbpo.repo;
import org.example.lab1rbpo.entity.User;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> storage = new HashMap<>();
    private Long idCounter = 1L;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        storage.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}