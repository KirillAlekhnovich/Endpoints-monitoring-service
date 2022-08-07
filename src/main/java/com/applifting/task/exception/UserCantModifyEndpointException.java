package com.applifting.task.exception;

public class UserCantModifyEndpointException extends RuntimeException {
    public UserCantModifyEndpointException() {
        super("This user is not allowed to modify this endpoint.");
    }
}
