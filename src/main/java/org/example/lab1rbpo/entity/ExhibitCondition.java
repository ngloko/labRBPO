package org.example.lab1rbpo.entity;

/**
 * Физическое состояние экспоната.
 * В состояниях RESTORATION и LOST экспонат нельзя выставлять.
 */
public enum ExhibitCondition {
    EXCELLENT,
    GOOD,
    DAMAGED,
    RESTORATION,
    LOST
}
