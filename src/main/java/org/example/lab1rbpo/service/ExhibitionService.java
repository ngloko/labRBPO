package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.ExhibitionRequest;
import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitionRepository;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CRUD-операции над выставками.
 */
@Service
public class ExhibitionService {

    private final ExhibitionRepository repository;
    private final UserRepository userRepository;
    private final ExhibitPlacementRepository placementRepository;

    public ExhibitionService(ExhibitionRepository repository,
                             UserRepository userRepository,
                             ExhibitPlacementRepository placementRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.placementRepository = placementRepository;
    }

    @Transactional(readOnly = true)
    public List<Exhibition> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Exhibition getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Выставка не найдена: id=" + id));
    }

    @Transactional
    public Exhibition create(ExhibitionRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessRuleException("Название выставки обязательно");
        }
        checkPeriod(request.startDate(), request.endDate());
        User curator = resolveCurator(request.curatorId());

        Exhibition exhibition = new Exhibition();
        exhibition.setName(request.name());
        exhibition.setStartDate(request.startDate());
        exhibition.setEndDate(request.endDate());
        exhibition.setStatus(request.status() == null || request.status().isBlank()
                ? ExhibitionStatus.PLANNED
                : parseStatus(request.status()));
        exhibition.setCurator(curator);
        return repository.save(exhibition);
    }

    @Transactional
    public Exhibition update(Long id, ExhibitionRequest request) {
        Exhibition existing = getById(id);

        if (request.name() != null && !request.name().isBlank()) {
            existing.setName(request.name());
        }

        LocalDateTime start = request.startDate() != null ? request.startDate() : existing.getStartDate();
        LocalDateTime end = request.endDate() != null ? request.endDate() : existing.getEndDate();
        if (request.startDate() != null || request.endDate() != null) {
            checkPeriod(start, end);
            existing.setStartDate(start);
            existing.setEndDate(end);
        }

        if (request.status() != null && !request.status().isBlank()) {
            ExhibitionStatus newStatus = parseStatus(request.status());
            if (existing.getStatus() == ExhibitionStatus.COMPLETED && newStatus != ExhibitionStatus.COMPLETED) {
                throw new BusinessRuleException("Завершённую выставку нельзя вернуть в другой статус");
            }
            existing.setStatus(newStatus);
        }

        if (request.curatorId() != null) {
            existing.setCurator(resolveCurator(request.curatorId()));
        }

        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Exhibition exhibition = getById(id);
        if (exhibition.getStatus() == ExhibitionStatus.ACTIVE) {
            throw new BusinessRuleException("Нельзя удалить активную выставку: сначала закройте её");
        }
        placementRepository.deleteByExhibition_Id(exhibition.getId());
        repository.delete(exhibition);
    }

    private User resolveCurator(Long curatorId) {
        if (curatorId == null) {
            throw new BusinessRuleException("Куратор обязателен");
        }
        return userRepository.findById(curatorId)
                .orElseThrow(() -> new NotFoundException("Куратор не найден: id=" + curatorId));
    }

    private static void checkPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BusinessRuleException("Даты начала и окончания обязательны");
        }
        if (!end.isAfter(start)) {
            throw new BusinessRuleException("Дата окончания должна быть позже даты начала");
        }
    }

    private static ExhibitionStatus parseStatus(String status) {
        try {
            return ExhibitionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Неизвестный статус выставки: " + status);
        }
    }
}
