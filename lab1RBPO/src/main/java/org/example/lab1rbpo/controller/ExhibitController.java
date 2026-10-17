package org.example.lab1rbpo.controller;

import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.service.ExhibitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exhibits")
public class ExhibitController {
    private final ExhibitService service;

    public ExhibitController(ExhibitService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Exhibit>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exhibit> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Exhibit> create(@RequestBody Exhibit exhibit) {
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