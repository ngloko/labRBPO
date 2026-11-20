package org.example.lab1rbpo.security;

/** Подпись/срок/обязательные claims/тип токена не прошли проверку. */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}