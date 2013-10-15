package com.schibsted.android.sdk.exceptions;

import java.util.Map;

/**
 * Signals that a API exception has occurred in SPiD
 */
public class SPiDApiException extends SPiDException {

    /**
     * Constructs a new SPiDApiException with the specified error, description, errorCode and type.
     *
     * @param error         The error as a string, see predefined constants in this class
     * @param descriptions  The detail messages
     * @param errorCode     The error code
     * @param type          The error type
     */
    public SPiDApiException(String error, Map<String, String> descriptions, Integer errorCode, String type) {
        super(error, descriptions, errorCode, type);
    }
}
