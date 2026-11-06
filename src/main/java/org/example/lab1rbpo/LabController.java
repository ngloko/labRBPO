package org.example.lab1rbpo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Служебные эндпоинты, доступные без аутентификации.
 */
@RestController
@RequestMapping("/api")
public class LabController {

    @GetMapping("/hello")
    public String hello() {
        return "Охаешечки датебаешечки. Поанимешимся?";
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "status", "REST API работает поверх PostgreSQL",
                "timestamp", LocalDateTime.now().toString(),
                "application", "lab1RBPO"
        );
    }
}
