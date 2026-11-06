package org.example.lab1rbpo.service;

import org.example.lab1rbpo.dto.PlaceExhibitRequest;
import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.entity.ExhibitCondition;
import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.example.lab1rbpo.entity.PlacementStatus;
import org.example.lab1rbpo.entity.Role;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.exception.BusinessRuleException;
import org.example.lab1rbpo.exception.NotFoundException;
import org.example.lab1rbpo.repo.ExhibitPlacementRepository;
import org.example.lab1rbpo.repo.ExhibitRepository;
import org.example.lab1rbpo.repo.ExhibitionHallRepository;
import org.example.lab1rbpo.repo.ExhibitionRepository;
import org.example.lab1rbpo.repo.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Бизнес-операции музея. Каждая операция:
 * <ul>
 *     <li>затрагивает минимум две сущности;</li>
 *     <li>проверяет текущее состояние связанных объектов;</li>
 *     <li>выполняется в одной транзакции и оставляет данные согласованными;</li>
 *     <li>может быть недопустима в зависимости от статусов;</li>
 *     <li>для операций над конкретной выставкой — доступна только её куратору или ADMIN.</li>
 * </ul>
 */
@Service
public class MuseumBusinessService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitRepository exhibitRepository;
    private final ExhibitionHallRepository hallRepository;
    private final ExhibitPlacementRepository placementRepository;
    private final UserRepository userRepository;

    public MuseumBusinessService(ExhibitionRepository exhibitionRepository,
                                 ExhibitRepository exhibitRepository,
                                 ExhibitionHallRepository hallRepository,
                                 ExhibitPlacementRepository placementRepository,
                                 UserRepository userRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.exhibitRepository = exhibitRepository;
        this.hallRepository = hallRepository;
        this.placementRepository = placementRepository;
        this.userRepository = userRepository;
    }

    /**
     * 1. Разместить экспонат на выставке в выбранном зале.
     * Проверяет статус выставки, состояние экспоната, доступность зала и свободную площадь.
     */
    @Transactional
    public ExhibitPlacement placeExhibit(PlaceExhibitRequest request) {
        Exhibition exhibition = loadExhibition(request.exhibitionId());
        requireExhibitionAccess(exhibition);
        Exhibit exhibit = loadExhibit(request.exhibitId());
        ExhibitionHall hall = loadHall(request.hallId());

        if (exhibition.getStatus() != ExhibitionStatus.PLANNED
                && exhibition.getStatus() != ExhibitionStatus.ACTIVE) {
            throw new BusinessRuleException("Нельзя размещать экспонаты на выставке в статусе "
                    + exhibition.getStatus());
        }
        if (exhibit.getCondition() == ExhibitCondition.RESTORATION
                || exhibit.getCondition() == ExhibitCondition.LOST) {
            throw new BusinessRuleException("Экспонат в состоянии " + exhibit.getCondition()
                    + " нельзя выставлять");
        }
        if (!Boolean.TRUE.equals(hall.getIsAvailable())) {
            throw new BusinessRuleException("Зал '" + hall.getName() + "' закрыт для размещения");
        }
        if (placementRepository.existsByExhibit_IdAndExhibition_IdAndStatusNot(
                exhibit.getId(), exhibition.getId(), PlacementStatus.REMOVED)) {
            throw new BusinessRuleException("Экспонат уже размещён на этой выставке");
        }
        if (placementRepository.existsByExhibit_IdAndStatusNotAndExhibition_Status(
                exhibit.getId(), PlacementStatus.REMOVED, ExhibitionStatus.ACTIVE)) {
            throw new BusinessRuleException("Экспонат уже участвует в активной выставке");
        }

        BigDecimal freeArea = freeArea(hall);
        if (freeArea.compareTo(exhibit.getRequiredArea()) < 0) {
            throw new BusinessRuleException(String.format(
                    "В зале '%s' недостаточно места: свободно %s м², требуется %s м²",
                    hall.getName(), freeArea, exhibit.getRequiredArea()));
        }

        ExhibitPlacement placement = new ExhibitPlacement();
        placement.setExhibit(exhibit);
        placement.setExhibition(exhibition);
        placement.setHall(hall);
        placement.setStatus(exhibition.getStatus() == ExhibitionStatus.ACTIVE
                ? PlacementStatus.INSTALLED
                : PlacementStatus.PLANNED);
        placement.setPlacedAt(LocalDateTime.now());
        return placementRepository.save(placement);
    }

    /**
     * 2. Переместить экспонат в другой зал в рамках той же выставки.
     * Проверяет, что выставка не завершена, зал доступен и вмещает экспонат.
     */
    @Transactional
    public ExhibitPlacement moveExhibit(Long placementId, Long newHallId) {
        ExhibitPlacement placement = loadPlacement(placementId);
        if (placement.getStatus() == PlacementStatus.REMOVED) {
            throw new BusinessRuleException("Экспонат уже снят с выставки");
        }

        Exhibition exhibition = placement.getExhibition();
        requireExhibitionAccess(exhibition);
        if (exhibition.getStatus() != ExhibitionStatus.PLANNED
                && exhibition.getStatus() != ExhibitionStatus.ACTIVE) {
            throw new BusinessRuleException("Нельзя перемещать экспонаты на выставке в статусе "
                    + exhibition.getStatus());
        }

        ExhibitionHall target = loadHall(newHallId);
        if (target.getId().equals(placement.getHall().getId())) {
            throw new BusinessRuleException("Экспонат уже находится в зале '" + target.getName() + "'");
        }
        if (!Boolean.TRUE.equals(target.getIsAvailable())) {
            throw new BusinessRuleException("Зал '" + target.getName() + "' закрыт для размещения");
        }

        BigDecimal freeArea = freeArea(target);
        if (freeArea.compareTo(placement.getExhibit().getRequiredArea()) < 0) {
            throw new BusinessRuleException(String.format(
                    "В зале '%s' недостаточно места: свободно %s м², требуется %s м²",
                    target.getName(), freeArea, placement.getExhibit().getRequiredArea()));
        }

        placement.setHall(target);
        if (exhibition.getStatus() == ExhibitionStatus.ACTIVE) {
            placement.setStatus(PlacementStatus.INSTALLED);
        }
        return placementRepository.save(placement);
    }

    /**
     * 3. Открыть выставку: PLANNED -> ACTIVE.
     * Требует, чтобы наступила дата начала, были экспонаты и все залы были доступны.
     * Все размещения переводятся в статус INSTALLED.
     */
    @Transactional
    public Exhibition openExhibition(Long exhibitionId) {
        Exhibition exhibition = loadExhibition(exhibitionId);
        requireExhibitionAccess(exhibition);
        if (exhibition.getStatus() != ExhibitionStatus.PLANNED) {
            throw new BusinessRuleException("Открыть можно только выставку в статусе PLANNED, текущий: "
                    + exhibition.getStatus());
        }
        if (exhibition.getStartDate().isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Дата начала выставки ещё не наступила: "
                    + exhibition.getStartDate());
        }

        List<ExhibitPlacement> placements = placementRepository.findByExhibition_Id(exhibitionId);
        if (placements.isEmpty()) {
            throw new BusinessRuleException("На выставке нет ни одного экспоната");
        }

        for (ExhibitPlacement placement : placements) {
            if (placement.getStatus() == PlacementStatus.REMOVED) {
                throw new BusinessRuleException("На выставке есть снятые размещения, откройте выставку заново");
            }
            ExhibitionHall hall = placement.getHall();
            if (!Boolean.TRUE.equals(hall.getIsAvailable())) {
                throw new BusinessRuleException("Зал '" + hall.getName() + "' закрыт, выставку нельзя открыть");
            }
            placement.setStatus(PlacementStatus.INSTALLED);
            if (placement.getPlacedAt() == null) {
                placement.setPlacedAt(LocalDateTime.now());
            }
        }
        placementRepository.saveAll(placements);

        exhibition.setStatus(ExhibitionStatus.ACTIVE);
        return exhibitionRepository.save(exhibition);
    }

    /**
     * 4. Закрыть выставку: ACTIVE -> COMPLETED.
     * Все размещения демонтируются, выставка завершается.
     */
    @Transactional
    public Exhibition closeExhibition(Long exhibitionId) {
        Exhibition exhibition = loadExhibition(exhibitionId);
        requireExhibitionAccess(exhibition);
        if (exhibition.getStatus() != ExhibitionStatus.ACTIVE) {
            throw new BusinessRuleException("Закрыть можно только активную выставку, текущий статус: "
                    + exhibition.getStatus());
        }

        List<ExhibitPlacement> placements = placementRepository.findByExhibition_Id(exhibitionId);
        LocalDateTime now = LocalDateTime.now();
        for (ExhibitPlacement placement : placements) {
            if (placement.getStatus() != PlacementStatus.REMOVED) {
                placement.setStatus(PlacementStatus.REMOVED);
                placement.setRemovedAt(now);
            }
        }
        placementRepository.saveAll(placements);

        exhibition.setStatus(ExhibitionStatus.COMPLETED);
        return exhibitionRepository.save(exhibition);
    }

    /**
     * 5. Отправить экспонат на реставрацию.
     * Недопустимо, если экспонат участвует в активной выставке; из запланированных
     * выставок он при этом снимается.
     */
    @Transactional
    public Exhibit sendToRestoration(Long exhibitId) {
        Exhibit exhibit = loadExhibit(exhibitId);

        if (exhibit.getCondition() == ExhibitCondition.RESTORATION) {
            throw new BusinessRuleException("Экспонат уже находится на реставрации");
        }
        if (exhibit.getCondition() == ExhibitCondition.LOST) {
            throw new BusinessRuleException("Утраченный экспонат нельзя отправить на реставрацию");
        }
        if (placementRepository.existsByExhibit_IdAndStatusNotAndExhibition_Status(
                exhibitId, PlacementStatus.REMOVED, ExhibitionStatus.ACTIVE)) {
            throw new BusinessRuleException(
                    "Экспонат участвует в активной выставке: сначала закройте выставку");
        }

        List<ExhibitPlacement> placements = placementRepository.findByExhibit_Id(exhibitId);
        LocalDateTime now = LocalDateTime.now();
        for (ExhibitPlacement placement : placements) {
            if (placement.getStatus() != PlacementStatus.REMOVED
                    && placement.getExhibition().getStatus() == ExhibitionStatus.PLANNED) {
                placement.setStatus(PlacementStatus.REMOVED);
                placement.setRemovedAt(now);
            }
        }
        placementRepository.saveAll(placements);

        exhibit.setCondition(ExhibitCondition.RESTORATION);
        return exhibitRepository.save(exhibit);
    }

    /**
     * Куратор может работать только со своими выставками, ADMIN — с любыми.
     */
    private void requireExhibitionAccess(Exhibition exhibition) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Пользователь не найден"));

        if (currentUser.getRole() != Role.ADMIN
                && !exhibition.getCurator().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Доступно только куратору этой выставки или администратору");
        }
    }

    private BigDecimal freeArea(ExhibitionHall hall) {
        BigDecimal occupied = placementRepository.sumRequiredAreaByHall(
                hall.getId(), PlacementStatus.REMOVED);
        return hall.getArea().subtract(occupied);
    }

    private Exhibition loadExhibition(Long id) {
        return exhibitionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Выставка не найдена: id=" + id));
    }

    private Exhibit loadExhibit(Long id) {
        return exhibitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Экспонат не найден: id=" + id));
    }

    private ExhibitionHall loadHall(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Зал не найден: id=" + id));
    }

    private ExhibitPlacement loadPlacement(Long id) {
        return placementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Размещение не найдено: id=" + id));
    }
}