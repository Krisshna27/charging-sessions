package io.everon.assignment.exception;

import io.everon.assignment.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Intercepts declared exceptions and provides custom error messages with appropriate HTTP response codes. <br>
 *
 * @author Krisshna Kumar Mone
 */
@Slf4j
@ControllerAdvice
public class MvcExceptionHandler {

    /**
     * Handle constraint violations
     * Responds with HTTP status 400 (BAD REQUEST)
     *
     * @param ex javax.validation.ConstraintViolationException
     * @return List of violation error messages
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> validationErrorHandler(ConstraintViolationException ex) {
        List<String> errorsList = new ArrayList<>(ex.getConstraintViolations().size());
        ex.getConstraintViolations().forEach(constraintViolation ->
                errorsList.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage()));

        ErrorResponseDto errorMessage = ErrorResponseDto.builder().errors(errorsList)
                .status(BAD_REQUEST.toString()).timeStamp(LocalDateTime.now().toString()).build();

        log.error("Constraint violations: {}", errorMessage);

        return ResponseEntity.status(BAD_REQUEST).body(errorMessage);
    }

    /**
     *Invalid parameter and request payload attributes will be treated as bind errors.
     *
     * @param ex org.springframework.validation.BindException
     * @return List of violation error messages
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> bindErrorHandler(BindException ex) {
        List<String> errorsList = new ArrayList<>(ex.getAllErrors().size());
        ex.getBindingResult().getFieldErrors().forEach( fieldError ->
                errorsList.add(fieldError.getField() +" : " + fieldError.getDefaultMessage()));

        ex.getBindingResult().getGlobalErrors().forEach( fieldError ->
                errorsList.add(fieldError.getObjectName() +" : " + fieldError.getDefaultMessage()));

        ErrorResponseDto errorMessage = ErrorResponseDto.builder().errors(errorsList)
                .status(BAD_REQUEST.toString()).timeStamp(LocalDateTime.now().toString()).build();

        log.error("Bind exception: {}", errorMessage);

        return ResponseEntity.status(BAD_REQUEST).body(errorMessage);
    }

    /**
     * If a requested resource is not found, NotFoundException would be thrown.<br>
     * This handler handles the exception and responds with HTTP status 404 (NOT_FOUND)
     *
     * @param ex NotFoundException thrown from the service implementations
     * @return List of violation error messages
     * @see NotFoundException
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> notFoundExceptionHandler(NotFoundException ex) {

        ErrorResponseDto errorMessage = ErrorResponseDto.builder().errors(singletonList(ex.getMessage()))
                .status(NOT_FOUND.toString()).timeStamp(LocalDateTime.now().toString()).build();

        log.error("NotFoundException : {}", errorMessage);

        return ResponseEntity.status(NOT_FOUND).body(errorMessage);
    }
}
