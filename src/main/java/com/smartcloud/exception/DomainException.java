package com.smartcloud.exception;

/**
 * Base class for domain-specific exceptions.
 */
public abstract class DomainException extends RuntimeException {
    protected DomainException() { super(); }
    protected DomainException(String message) { super(message); }
    protected DomainException(String message, Throwable cause) { super(message, cause); }
    protected DomainException(Throwable cause) { super(cause); }
}
