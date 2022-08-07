package com.applifting.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private Map<String, Object> generateExceptionBody(Exception exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        body.put("message", exception.getMessage());
        return body;
    }

    @ExceptionHandler(EmailIsNotValidException.class)
    public ResponseEntity<Object> handleEmailIsNotValidException(EmailIsNotValidException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(EndpointAlreadyExistsException.class)
    public ResponseEntity<Object> handleEndpointAlreadyExistsException(EndpointAlreadyExistsException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EndpointCanNotBeModifiedException.class)
    public ResponseEntity<Object> handleEndpointCanNotBeModifiedException(EndpointCanNotBeModifiedException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EndpointDoesNotExistException.class)
    public ResponseEntity<Object> handleEndpointDoesNotExistException(EndpointDoesNotExistException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidMonitoringIntervalException.class)
    public ResponseEntity<Object> handleInvalidMonitoringIntervalException(InvalidMonitoringIntervalException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ParameterMissingException.class)
    public ResponseEntity<Object> handleParameterMissingException(ParameterMissingException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ResultDoesNotExistException.class)
    public ResponseEntity<Object> handleResultDoesNotExistException(ResultDoesNotExistException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenIsMissingException.class)
    public ResponseEntity<Object> handleTokenIsMissingException(TokenIsMissingException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserCantModifyEndpointException.class)
    public ResponseEntity<Object> handleUserIsNotAllowedToModifyEndpointException(UserCantModifyEndpointException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<Object> handleUserDoesNotExistException(UserDoesNotExistException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserHasNoEndpointsException.class)
    public ResponseEntity<Object> handleUserHasNoMonitoredEndpointsException(UserHasNoEndpointsException exception) {
        return new ResponseEntity<>(generateExceptionBody(exception), HttpStatus.NO_CONTENT);
    }
}
