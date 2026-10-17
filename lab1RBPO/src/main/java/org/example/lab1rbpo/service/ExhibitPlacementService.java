package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.repo.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExhibitPlacementService {
    private final ExhibitPlacementRepository repository;
    private final ExhibitRepository exhibitRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionHallRepository hallRepository;

    public ExhibitPlacementService(ExhibitPlacementRepository repository,
                                   ExhibitRepository exhibitRepository,
                                   ExhibitionRepository exhibitionRepository,
                                   ExhibitionHallRepository hallRepository) {
        this.repository = repository;
        this.exhibitRepository = exhibitRepository;
        this.exhibitionRepository = exhibitionRepository;
        this.hallRepository = hallRepository;
    }

    public List<ExhibitPlacement> getAll() { return repository.findAll(); }

    public ExhibitPlacement getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Размещение не найдено"));
    }

    public ExhibitPlacement create(ExhibitPlacement placement) {
        validateLinks(placement);
        return repository.save(placement);
    }

    public ExhibitPlacement update(Long id, ExhibitPlacement placement) {
        ExhibitPlacement existing = getById(id);
        if (placement.getExhibitId() != null) existing.setExhibitId(placement.getExhibitId());
        if (placement.getExhibitionId() != null) existing.setExhibitionId(placement.getExhibitionId());
        if (placement.getHallId() != null) existing.setHallId(placement.getHallId());
        if (placement.getInstallationStatus() != null) existing.setInstallationStatus(placement.getInstallationStatus());
        validateLinks(existing);
        return repository.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        repository.deleteById(id);
    }

    private void validateLinks(ExhibitPlacement placement) {
        if (!exhibitRepository.existsById(placement.getExhibitId())) {
            throw new RuntimeException("Экспонат не найден");
        }
        if (!exhibitionRepository.existsById(placement.getExhibitionId())) {
            throw new RuntimeException("Выставка не найдена");
        }
        if (!hallRepository.existsById(placement.getHallId())) {
            throw new RuntimeException("Зал не найден");
        }
    }
}