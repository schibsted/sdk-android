package com.schibsted.android.sdk.exceptions;

/**
 * Signals that a API exception has occurred in SPiD
 */
public class SPiDApiException extends SPiDException {

    /**
     * Constructs a new SPiDApiException with the specified error, description, errorCode and type.
     *
     * @param error       The error as a string, see predefined constants in this class
     * @param description The detail message
     * @param errorCode   The error code
     * @param type        The error type
     */
    public SPiDApiException(String error, String description, Integer errorCode, String type) {
        super(error, description, errorCode, type);
    }
}
