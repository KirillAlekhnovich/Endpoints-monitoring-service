package com.applifting.task.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User with this access token already exists.");
    }

    public UserAlreadyExistsException(String username) {
        super("User with username " + username + " already exists.");
    }
}
