package org.example.lab1rbpo.controller;

import jakarta.validation.Valid;
import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.service.ExhibitService;
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
 * CRUD экспонатов.
 */
@RestController
@RequestMapping("/api/exhibits")
public class ExhibitController {

    private final ExhibitService service;

    public ExhibitController(ExhibitService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Exhibit>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exhibit> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Exhibit> create(@Valid @RequestBody Exhibit exhibit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(exhibit));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Exhibit> update(@PathVariable Long id, @RequestBody Exhibit exhibit) {
        return ResponseEntity.ok(service.update(id, exhibit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
