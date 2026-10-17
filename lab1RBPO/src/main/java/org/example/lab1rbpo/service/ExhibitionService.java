package org.example.lab1rbpo.service;

import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitionRepository;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<Exhibition> getAll() { return repository.findAll(); }

    public Exhibition getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Выставка не найдена"));
    }

    public Exhibition create(Exhibition exhibition) {
        // Проверяем, что куратор существует
        if (exhibition.getCuratorId() != null && !userRepository.existsById(exhibition.getCuratorId())) {
            throw new RuntimeException("Куратор с id " + exhibition.getCuratorId() + " не найден");
        }
        return repository.save(exhibition);
    }

    public Exhibition update(Long id, Exhibition exhibition) {
        Exhibition existing = getById(id);
        existing.setName(exhibition.getName());
        existing.setStartDate(exhibition.getStartDate());
        existing.setEndDate(exhibition.getEndDate());
        existing.setStatus(exhibition.getStatus());
        if (exhibition.getCuratorId() != null) {
            if (!userRepository.existsById(exhibition.getCuratorId())) {
                throw new RuntimeException("Куратор не найден");
            }
            existing.setCuratorId(exhibition.getCuratorId());
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        // Каскад: удаляем все размещения на этой выставке
        placementRepository.deleteByExhibitionId(id);
        repository.deleteById(id);
    }
}