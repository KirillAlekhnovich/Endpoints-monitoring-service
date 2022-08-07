package com.applifting.task.exception;

public class InvalidMonitoringIntervalException extends RuntimeException {
    public InvalidMonitoringIntervalException() {
        super("Entered monitoring interval is not valid. It can't be below zero.");
    }
}
