package com.applifting.task.exception;

public class EndpointAlreadyExistsException extends RuntimeException {
    public EndpointAlreadyExistsException(Long id) {
        super("User already monitors an endpoint with this URL (it's id is " + id + ").");
    }
}
