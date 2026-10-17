package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.Exhibit;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class ExhibitRepository {
    private final Map<Long, Exhibit> storage = new HashMap<>();
    private Long idCounter = 1L;

    public Exhibit save(Exhibit exhibit) {
        if (exhibit.getId() == null) {
            exhibit.setId(idCounter++);
        }
        storage.put(exhibit.getId(), exhibit);
        return exhibit;
    }

    public Optional<Exhibit> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Exhibit> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}