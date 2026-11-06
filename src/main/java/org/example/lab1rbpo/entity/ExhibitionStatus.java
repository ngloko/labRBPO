package org.example.lab1rbpo.entity;

/**
 * Жизненный цикл выставки: PLANNED -> ACTIVE -> COMPLETED, либо PLANNED -> CANCELLED.
 */
public enum ExhibitionStatus {
    PLANNED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}
