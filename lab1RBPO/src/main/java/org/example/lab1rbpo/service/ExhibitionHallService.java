package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.repo.ExhibitionHallRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExhibitionHallService {
    private final ExhibitionHallRepository repository;

    public ExhibitionHallService(ExhibitionHallRepository repository) {
        this.repository = repository;
    }

    public List<ExhibitionHall> getAll() { return repository.findAll(); }

    public ExhibitionHall getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Зал не найден"));
    }

    public ExhibitionHall create(ExhibitionHall hall) { return repository.save(hall); }

    public ExhibitionHall update(Long id, ExhibitionHall hall) {
        ExhibitionHall existing = getById(id);
        existing.setArea(hall.getArea());
        existing.setIsAvailable(hall.getIsAvailable());
        return repository.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        repository.deleteById(id);
    }
}