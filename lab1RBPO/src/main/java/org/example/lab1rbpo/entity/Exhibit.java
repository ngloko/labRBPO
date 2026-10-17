package org.example.lab1rbpo.entity;
import lombok.Data;

@Data
public class Exhibit {
    private Long id;
    private String name;
    private String condition;
    private Double requiredArea;
}