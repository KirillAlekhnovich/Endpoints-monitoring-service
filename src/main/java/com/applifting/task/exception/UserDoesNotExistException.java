package com.applifting.task.exception;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
        super("User with this access token does not exist.");
    }
}
