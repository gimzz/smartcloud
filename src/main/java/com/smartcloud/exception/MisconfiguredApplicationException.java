package com.smartcloud.exception;

public class MisconfiguredApplicationException extends DomainException {
    public MisconfiguredApplicationException() { super(); }
    public MisconfiguredApplicationException(String message) { super(message); }
    public MisconfiguredApplicationException(String message, Throwable cause) { super(message, cause); }
    public MisconfiguredApplicationException(Throwable cause) { super(cause); }
}
