package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.example.lab1rbpo.entity.PlacementStatus;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * CRUD-операции над экспонатами.
 */
@Service
public class ExhibitService {

    private final ExhibitRepository repository;
    private final ExhibitPlacementRepository placementRepository;

    public ExhibitService(ExhibitRepository repository, ExhibitPlacementRepository placementRepository) {
        this.repository = repository;
        this.placementRepository = placementRepository;
    }

    @Transactional(readOnly = true)
    public List<Exhibit> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Exhibit getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Экспонат не найден: id=" + id));
    }

    @Transactional
    public Exhibit create(Exhibit exhibit) {
        if (exhibit.getInventoryNumber() == null || exhibit.getInventoryNumber().isBlank()) {
            throw new BusinessRuleException("Инвентарный номер обязателен");
        }
        if (repository.existsByInventoryNumber(exhibit.getInventoryNumber())) {
            throw new BusinessRuleException("Экспонат с инвентарным номером '"
                    + exhibit.getInventoryNumber() + "' уже существует");
        }
        if (exhibit.getRequiredArea() == null || exhibit.getRequiredArea().signum() <= 0) {
            throw new BusinessRuleException("Требуемая площадь должна быть положительной");
        }
        exhibit.setId(null);
        return repository.save(exhibit);
    }

    @Transactional
    public Exhibit update(Long id, Exhibit exhibit) {
        Exhibit existing = getById(id);

        if (exhibit.getInventoryNumber() != null && !exhibit.getInventoryNumber().isBlank()
                && !exhibit.getInventoryNumber().equals(existing.getInventoryNumber())) {
            if (repository.existsByInventoryNumber(exhibit.getInventoryNumber())) {
                throw new BusinessRuleException("Экспонат с инвентарным номером '"
                        + exhibit.getInventoryNumber() + "' уже существует");
            }
            existing.setInventoryNumber(exhibit.getInventoryNumber());
        }
        if (exhibit.getName() != null && !exhibit.getName().isBlank()) {
            existing.setName(exhibit.getName());
        }
        if (exhibit.getCondition() != null) {
            existing.setCondition(exhibit.getCondition());
        }
        if (exhibit.getRequiredArea() != null) {
            BigDecimal area = exhibit.getRequiredArea();
            if (area.signum() <= 0) {
                throw new BusinessRuleException("Требуемая площадь должна быть положительной");
            }
            existing.setRequiredArea(area);
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Exhibit exhibit = getById(id);
        if (placementRepository.existsByExhibit_IdAndStatusNotAndExhibition_Status(
                id, PlacementStatus.REMOVED, ExhibitionStatus.ACTIVE)) {
            throw new BusinessRuleException(
                    "Нельзя удалить экспонат: он участвует в активной выставке");
        }
        placementRepository.deleteByExhibit_Id(exhibit.getId());
        repository.delete(exhibit);
    }
}
