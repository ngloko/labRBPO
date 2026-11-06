package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.example.lab1rbpo.entity.PlacementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Имена методов используют "_" для явного перехода по связям:
 * Exhibit_Id -> placement.exhibit.id.
 */
public interface ExhibitPlacementRepository extends JpaRepository<ExhibitPlacement, Long> {

    List<ExhibitPlacement> findByExhibition_Id(Long exhibitionId);

    List<ExhibitPlacement> findByExhibit_Id(Long exhibitId);

    boolean existsByExhibit_IdAndExhibition_IdAndStatusNot(Long exhibitId,
                                                           Long exhibitionId,
                                                           PlacementStatus status);

    boolean existsByExhibit_IdAndExhibition_IdAndStatusNotAndIdNot(Long exhibitId,
                                                                   Long exhibitionId,
                                                                   PlacementStatus status,
                                                                   Long id);

    boolean existsByHall_Id(Long hallId);

    boolean existsByHall_IdAndStatusNot(Long hallId, PlacementStatus status);

    boolean existsByExhibit_IdAndStatusNot(Long exhibitId, PlacementStatus status);

    /** Есть ли у экспоната действующее размещение на выставке с указанным статусом. */
    boolean existsByExhibit_IdAndStatusNotAndExhibition_Status(Long exhibitId,
                                                               PlacementStatus status,
                                                               ExhibitionStatus exhibitionStatus);

    /** Занятая площадь зала: сумма requiredArea всех не снятых размещений. */
    @Query("""
            select coalesce(sum(p.exhibit.requiredArea), 0)
            from ExhibitPlacement p
            where p.hall.id = :hallId
              and p.status <> :removed
            """)
    BigDecimal sumRequiredAreaByHall(@Param("hallId") Long hallId,
                                     @Param("removed") PlacementStatus removed);

    void deleteByExhibit_Id(Long exhibitId);

    void deleteByExhibition_Id(Long exhibitionId);
}
