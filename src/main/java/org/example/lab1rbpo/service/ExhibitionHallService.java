package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitionHallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD-операции над выставочными залами.
 */
@Service
public class ExhibitionHallService {

    private final ExhibitionHallRepository repository;
    private final ExhibitPlacementRepository placementRepository;

    public ExhibitionHallService(ExhibitionHallRepository repository,
                                 ExhibitPlacementRepository placementRepository) {
        this.repository = repository;
        this.placementRepository = placementRepository;
    }

    @Transactional(readOnly = true)
    public List<ExhibitionHall> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ExhibitionHall getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Зал не найден: id=" + id));
    }

    @Transactional
    public ExhibitionHall create(ExhibitionHall hall) {
        if (hall.getName() == null || hall.getName().isBlank()) {
            throw new BusinessRuleException("Название зала обязательно");
        }
        if (repository.existsByName(hall.getName())) {
            throw new BusinessRuleException("Зал с названием '" + hall.getName() + "' уже существует");
        }
        if (hall.getIsAvailable() == null) {
            hall.setIsAvailable(Boolean.TRUE);
        }
        hall.setId(null);
        return repository.save(hall);
    }

    @Transactional
    public ExhibitionHall update(Long id, ExhibitionHall hall) {
        ExhibitionHall existing = getById(id);

        if (hall.getName() != null && !hall.getName().isBlank()
                && !hall.getName().equals(existing.getName())) {
            if (repository.existsByName(hall.getName())) {
                throw new BusinessRuleException("Зал с названием '" + hall.getName() + "' уже существует");
            }
            existing.setName(hall.getName());
        }
        if (hall.getArea() != null) {
            if (hall.getArea().signum() <= 0) {
                throw new BusinessRuleException("Площадь зала должна быть положительной");
            }
            existing.setArea(hall.getArea());
        }
        if (hall.getIsAvailable() != null) {
            existing.setIsAvailable(hall.getIsAvailable());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ExhibitionHall hall = getById(id);
        if (placementRepository.existsByHall_Id(id)) {
            throw new BusinessRuleException(
                    "Нельзя удалить зал: в нём есть размещения экспонатов (в том числе исторические)");
        }
        repository.delete(hall);
    }
}
