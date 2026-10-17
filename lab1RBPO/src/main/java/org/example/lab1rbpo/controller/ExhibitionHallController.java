package org.example.lab1rbpo.controller;

import org.example.lab1rbpo.entity.ExhibitionHall;
import org.example.lab1rbpo.service.ExhibitionHallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exhibition-halls")
public class ExhibitionHallController {
    private final ExhibitionHallService service;

    public ExhibitionHallController(ExhibitionHallService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<ExhibitionHall>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionHall> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ExhibitionHall> create(@RequestBody ExhibitionHall hall) {
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