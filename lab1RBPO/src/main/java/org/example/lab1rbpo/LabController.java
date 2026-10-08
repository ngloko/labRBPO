package org.example.lab1rbpo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

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
                "status", "Лабубу сделал вроде",
                "timestamp", LocalDateTime.now().toString(),
                "application", "lab1"
        );
    }
}