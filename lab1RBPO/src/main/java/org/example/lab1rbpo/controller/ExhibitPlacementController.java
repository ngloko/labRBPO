package org.example.lab1rbpo.controller;

import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.service.ExhibitPlacementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exhibit-placements")
public class ExhibitPlacementController {
    private final ExhibitPlacementService service;

    public ExhibitPlacementController(ExhibitPlacementService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<ExhibitPlacement>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitPlacement> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExhibitPlacement> create(@RequestBody ExhibitPlacement placement) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(placement));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExhibitPlacement> update(@PathVariable Long id, @RequestBody ExhibitPlacement placement) {
        return ResponseEntity.ok(service.update(id, placement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}