package org.example.lab1rbpo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Размещение экспоната на выставке в конкретном зале.
 * Пара (exhibit_id, exhibition_id) уникальна: один экспонат — одно размещение на выставке.
 */
@Entity
@Table(name = "exhibit_placement")
@Getter
@Setter
@NoArgsConstructor
public class ExhibitPlacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exhibit_id", nullable = false)
    private Exhibit exhibit;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private ExhibitionHall hall;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlacementStatus status;

    @Column(name = "placed_at")
    private LocalDateTime placedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @JsonProperty(value = "exhibitId", access = JsonProperty.Access.READ_ONLY)
    public Long getExhibitId() {
        return exhibit == null ? null : exhibit.getId();
    }

    @JsonProperty(value = "exhibitionId", access = JsonProperty.Access.READ_ONLY)
    public Long getExhibitionId() {
        return exhibition == null ? null : exhibition.getId();
    }

    @JsonProperty(value = "hallId", access = JsonProperty.Access.READ_ONLY)
    public Long getHallId() {
        return hall == null ? null : hall.getId();
    }
}
