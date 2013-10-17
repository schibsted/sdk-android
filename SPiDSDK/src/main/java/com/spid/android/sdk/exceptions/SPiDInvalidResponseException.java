package com.spid.android.sdk.exceptions;

/**
 * Signals that there was a problem with the response received from SPiD
 */
public class SPiDInvalidResponseException extends SPiDException {

    /**
     * Constructs a new SPiDInvalidResponseException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SPiDInvalidResponseException(String message) {
        super(message);
    }

    /**
     * Constructs a new SPiDInvalidResponseException with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param throwable The cause
     */
    public SPiDInvalidResponseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
