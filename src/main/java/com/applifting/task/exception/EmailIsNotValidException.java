package com.applifting.task.exception;

public class EmailIsNotValidException extends RuntimeException {
    public EmailIsNotValidException() {
        super("Users' email is not valid.");
    }
}
