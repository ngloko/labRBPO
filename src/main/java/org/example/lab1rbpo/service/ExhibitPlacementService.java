package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.PlacementRequest;
import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.example.lab1rbpo.entity.PlacementStatus;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitRepository;
import org.example.lab1rbpo.repo.ExhibitionHallRepository;
import org.example.lab1rbpo.repo.ExhibitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD-операции над размещениями экспонатов.
 * Бизнес-правила размещения реализованы в {@link MuseumBusinessService}.
 */
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

    @Transactional(readOnly = true)
    public List<ExhibitPlacement> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ExhibitPlacement getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Размещение не найдено: id=" + id));
    }

    @Transactional
    public ExhibitPlacement create(PlacementRequest request) {
        if (request.exhibitId() == null || request.exhibitionId() == null || request.hallId() == null) {
            throw new BusinessRuleException("exhibitId, exhibitionId и hallId обязательны");
        }
        Exhibit exhibit = loadExhibit(request.exhibitId());
        Exhibition exhibition = loadExhibition(request.exhibitionId());
        ExhibitionHall hall = loadHall(request.hallId());

        if (repository.existsByExhibit_IdAndExhibition_IdAndStatusNot(
                exhibit.getId(), exhibition.getId(), PlacementStatus.REMOVED)) {
            throw new BusinessRuleException("Экспонат уже размещён на этой выставке");
        }

        ExhibitPlacement placement = new ExhibitPlacement();
        placement.setExhibit(exhibit);
        placement.setExhibition(exhibition);
        placement.setHall(hall);
        placement.setStatus(request.status() == null || request.status().isBlank()
                ? defaultStatus(exhibition)
                : parseStatus(request.status()));
        placement.setPlacedAt(LocalDateTime.now());
        return repository.save(placement);
    }

    @Transactional
    public ExhibitPlacement update(Long id, PlacementRequest request) {
        ExhibitPlacement existing = getById(id);
        if (existing.getStatus() == PlacementStatus.REMOVED) {
            throw new BusinessRuleException("Нельзя изменить снятое размещение");
        }

        if (request.exhibitId() != null) {
            existing.setExhibit(loadExhibit(request.exhibitId()));
        }
        if (request.exhibitionId() != null) {
            existing.setExhibition(loadExhibition(request.exhibitionId()));
        }
        if (request.hallId() != null) {
            existing.setHall(loadHall(request.hallId()));
        }
        if (request.status() != null && !request.status().isBlank()) {
            existing.setStatus(parseStatus(request.status()));
        }

        if (repository.existsByExhibit_IdAndExhibition_IdAndStatusNotAndIdNot(
                existing.getExhibit().getId(), existing.getExhibition().getId(),
                PlacementStatus.REMOVED, id)) {
            throw new BusinessRuleException("Такое размещение уже существует");
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ExhibitPlacement placement = getById(id);
        repository.delete(placement);
    }

    private static PlacementStatus defaultStatus(Exhibition exhibition) {
        return exhibition.getStatus() == ExhibitionStatus.ACTIVE
                ? PlacementStatus.INSTALLED
                : PlacementStatus.PLANNED;
    }

    private static PlacementStatus parseStatus(String status) {
        try {
            return PlacementStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Неизвестный статус размещения: " + status);
        }
    }

    private Exhibit loadExhibit(Long id) {
        return exhibitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Экспонат не найден: id=" + id));
    }

    private Exhibition loadExhibition(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Выставка не найдена: id=" + id));
    }

    private ExhibitionHall loadHall(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Зал не найден: id=" + id));
    }
}
