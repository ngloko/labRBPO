package org.example.lab1rbpo.controller;

import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.service.ExhibitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {
    private final ExhibitionService service;

    public ExhibitionController(ExhibitionService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Exhibition>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exhibition> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Exhibition> create(@RequestBody Exhibition exhibition) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(exhibition));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Exhibition> update(@PathVariable Long id, @RequestBody Exhibition exhibition) {
        return ResponseEntity.ok(service.update(id, exhibition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}