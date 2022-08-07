package com.applifting.task.exception;

public class TokenIsMissingException extends RuntimeException {
    public TokenIsMissingException () {
        super("Access token was not found in header.");
    }
}
