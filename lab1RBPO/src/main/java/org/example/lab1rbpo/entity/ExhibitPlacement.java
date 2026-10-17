package org.example.lab1rbpo.entity;
import lombok.Data;

@Data
public class ExhibitPlacement {
    private Long id;
    private Long exhibitId;
    private Long exhibitionId;
    private Long hallId;
    private String installationStatus;
}