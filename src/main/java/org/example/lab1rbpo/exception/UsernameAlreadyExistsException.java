package org.example.lab1rbpo.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Учётная запись с именем '" + username + "' уже существует");
    }
}