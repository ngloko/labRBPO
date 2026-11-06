package org.example.lab1rbpo.exception;

/**
 * Нарушение бизнес-правила предметной области — HTTP 409.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
