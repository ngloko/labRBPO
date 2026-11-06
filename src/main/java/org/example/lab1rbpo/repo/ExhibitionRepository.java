package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.entity.ExhibitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    List<Exhibition> findByStatus(ExhibitionStatus status);

    boolean existsByCurator_Id(Long curatorId);
}
