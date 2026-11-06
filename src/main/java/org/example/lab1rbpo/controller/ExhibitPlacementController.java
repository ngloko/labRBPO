package org.example.lab1rbpo.controller;

import jakarta.validation.Valid;
import org.example.lab1rbpo.dto.PlacementRequest;
import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.service.ExhibitPlacementService;
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

@RestController
@RequestMapping("/api/exhibit-placements")
public class ExhibitPlacementController {

    private final ExhibitPlacementService service;

    public ExhibitPlacementController(ExhibitPlacementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExhibitPlacement>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitPlacement> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExhibitPlacement> create(@Valid @RequestBody PlacementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExhibitPlacement> update(@PathVariable Long id,
                                                   @Valid @RequestBody PlacementRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
