package com.applifting.task.exception;

public class ResultDoesNotExistException extends RuntimeException {
    public ResultDoesNotExistException(Long resultId) {
        super("Result with id " + resultId + " does not exist.");
    }
}
