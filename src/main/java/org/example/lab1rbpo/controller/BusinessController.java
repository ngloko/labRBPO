package org.example.lab1rbpo.controller;

import jakarta.validation.Valid;
import org.example.lab1rbpo.dto.MoveExhibitRequest;
import org.example.lab1rbpo.dto.PlaceExhibitRequest;
import org.example.lab1rbpo.entity.Exhibit;
import org.example.lab1rbpo.entity.ExhibitPlacement;
import org.example.lab1rbpo.entity.Exhibition;
import org.example.lab1rbpo.service.MuseumBusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
/**
 * Бизнес-операции предметной области.
 * Отличаются от CRUD: меняют состояние нескольких сущностей и проверяют бизнес-правила.
 */
@PreAuthorize("hasAnyRole('ADMIN','CURATOR')")
@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final MuseumBusinessService service;

    public BusinessController(MuseumBusinessService service) {
        this.service = service;
    }

    /** 1. Разместить экспонат на выставке в зале. */
    @PostMapping("/placements")
    public ResponseEntity<ExhibitPlacement> placeExhibit(@Valid @RequestBody PlaceExhibitRequest request) {
        return ResponseEntity.ok(service.placeExhibit(request));
    }

    /** 2. Переместить экспонат в другой зал. */
    @PostMapping("/placements/{id}/move")
    public ResponseEntity<ExhibitPlacement> moveExhibit(@PathVariable Long id,
                                                        @Valid @RequestBody MoveExhibitRequest request) {
        return ResponseEntity.ok(service.moveExhibit(id, request.hallId()));
    }

    /** 3. Открыть выставку. */
    @PostMapping("/exhibitions/{id}/open")
    public ResponseEntity<Exhibition> openExhibition(@PathVariable Long id) {
        return ResponseEntity.ok(service.openExhibition(id));
    }

    /** 4. Закрыть выставку. */
    @PostMapping("/exhibitions/{id}/close")
    public ResponseEntity<Exhibition> closeExhibition(@PathVariable Long id) {
        return ResponseEntity.ok(service.closeExhibition(id));
    }

    /** 5. Отправить экспонат на реставрацию. */
    @PostMapping("/exhibits/{id}/restoration")
    public ResponseEntity<Exhibit> sendToRestoration(@PathVariable Long id) {
        return ResponseEntity.ok(service.sendToRestoration(id));
    }
}
