package org.example.lab1rbpo.controller;

import jakarta.validation.Valid;
import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.service.ExhibitionHallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CRUD выставочных залов.
 */
@RestController
@RequestMapping("/api/halls")
public class ExhibitionHallController {

    private final ExhibitionHallService service;

    public ExhibitionHallController(ExhibitionHallService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExhibitionHall>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionHall> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExhibitionHall> create(@Valid @RequestBody ExhibitionHall hall) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(hall));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExhibitionHall> update(@PathVariable Long id, @RequestBody ExhibitionHall hall) {
        return ResponseEntity.ok(service.update(id, hall));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
