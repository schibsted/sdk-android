package com.spid.android.sdk.exceptions;

/**
 * Signals that a authorization request is already running
 */
public class SPiDAuthorizationAlreadyRunningException extends SPiDException {
    /**
     * Constructs a new SPiDAuthorizationAlreadyRunningException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SPiDAuthorizationAlreadyRunningException(String message) {
        super(message);
    }
}
