package org.example.lab1rbpo.repo;

import org.example.lab1rbpo.entity.ExhibitionHall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExhibitionHallRepository extends JpaRepository<ExhibitionHall, Long> {

    Optional<ExhibitionHall> findByName(String name);

    boolean existsByName(String name);
}
