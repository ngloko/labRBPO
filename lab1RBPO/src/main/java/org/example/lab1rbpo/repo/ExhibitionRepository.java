package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.Exhibition;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class ExhibitionRepository {
    private final Map<Long, Exhibition> storage = new HashMap<>();
    private Long idCounter = 1L;

    public Exhibition save(Exhibition exhibition) {
        if (exhibition.getId() == null) {
            exhibition.setId(idCounter++);
        }
        storage.put(exhibition.getId(), exhibition);
        return exhibition;
    }

    public Optional<Exhibition> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Exhibition> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}