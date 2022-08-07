package com.applifting.task.exception;

public class UserHasNoEndpointsException extends RuntimeException {
    public UserHasNoEndpointsException() {
        super("This user has no monitored endpoints.");
    }
}
