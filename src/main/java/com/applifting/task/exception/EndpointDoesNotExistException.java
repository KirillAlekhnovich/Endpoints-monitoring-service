package com.applifting.task.exception;

public class EndpointDoesNotExistException extends RuntimeException {
    public EndpointDoesNotExistException(Long id) {
        super("Monitored endpoint with id " + id + " does not exist.");
    }
}
