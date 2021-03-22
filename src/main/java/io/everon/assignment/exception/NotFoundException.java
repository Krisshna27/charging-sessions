package io.everon.assignment.exception;

/**
 * Exception to be thrown if a requested resource is not found
 *
 * @author Krisshna Kumar Mone
 */
public class NotFoundException extends RuntimeException{

    public NotFoundException(String message) {
        super(message);
    }
}
