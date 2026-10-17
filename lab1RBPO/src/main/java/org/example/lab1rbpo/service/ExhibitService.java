package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExhibitService {
    private final ExhibitRepository repository;
    private final ExhibitPlacementRepository placementRepository;

    public ExhibitService(ExhibitRepository repository, ExhibitPlacementRepository placementRepository) {
        this.repository = repository;
        this.placementRepository = placementRepository;
    }

    public List<Exhibit> getAll() { return repository.findAll(); }

    public Exhibit getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Экспонат не найден"));
    }

    public Exhibit create(Exhibit exhibit) { return repository.save(exhibit); }

    public Exhibit update(Long id, Exhibit exhibit) {
        Exhibit existing = getById(id);
        existing.setName(exhibit.getName());
        existing.setCondition(exhibit.getCondition());
        existing.setRequiredArea(exhibit.getRequiredArea());
        return repository.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        placementRepository.deleteByExhibitId(id);
        repository.deleteById(id);
    }
}