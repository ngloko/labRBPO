package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ExhibitPlacementRepository {
    private final Map<Long, ExhibitPlacement> storage = new HashMap<>();
    private Long idCounter = 1L;

    public ExhibitPlacement save(ExhibitPlacement placement) {
        if (placement.getId() == null) {
            placement.setId(idCounter++);
        }
        storage.put(placement.getId(), placement);
        return placement;
    }

    public Optional<ExhibitPlacement> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ExhibitPlacement> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    // Специальный метод для каскадного удаления
    public void deleteByExhibitionId(Long exhibitionId) {
        List<Long> toDelete = storage.values().stream()
                .filter(p -> p.getExhibitionId().equals(exhibitionId))
                .map(ExhibitPlacement::getId)
                .collect(Collectors.toList());
        toDelete.forEach(storage::remove);
    }

    public void deleteByExhibitId(Long exhibitId) {
        List<Long> toDelete = storage.values().stream()
                .filter(p -> p.getExhibitId().equals(exhibitId))
                .map(ExhibitPlacement::getId)
                .collect(Collectors.toList());
        toDelete.forEach(storage::remove);
    }
}
