package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.Exhibit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExhibitRepository extends JpaRepository<Exhibit, Long> {

    Optional<Exhibit> findByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumber(String inventoryNumber);
}
