package org.example.lab1rbpo.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Exhibition {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Long curatorId;
}