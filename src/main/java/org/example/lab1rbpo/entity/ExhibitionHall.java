package org.example.lab1rbpo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Выставочный зал. isAvailable = false — зал закрыт для новых размещений
 * (ремонт, техобслуживание), это не то же самое, что «зал пуст».
 */
@Entity
@Table(name = "exhibition_hall")
@Getter
@Setter
@NoArgsConstructor
public class ExhibitionHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Площадь зала должна быть положительной")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal area;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}
