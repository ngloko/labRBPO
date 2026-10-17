package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.ExhibitionHall;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class ExhibitionHallRepository {
    private final Map<Long, ExhibitionHall> storage = new HashMap<>();
    private Long idCounter = 1L;

    public ExhibitionHall save(ExhibitionHall hall) {
        if (hall.getId() == null) {
            hall.setId(idCounter++);
        }
        storage.put(hall.getId(), hall);
        return hall;
    }

    public Optional<ExhibitionHall> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ExhibitionHall> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
