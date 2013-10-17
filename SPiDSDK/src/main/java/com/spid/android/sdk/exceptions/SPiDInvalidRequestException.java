package com.spid.android.sdk.exceptions;

/**
 * Signals that there was a problem with the request to SPiD
 */
public class SPiDInvalidRequestException extends SPiDException {

    /**
     * Constructs a new SPiDInvalidRequestException with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param throwable The cause
     */
    public SPiDInvalidRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
