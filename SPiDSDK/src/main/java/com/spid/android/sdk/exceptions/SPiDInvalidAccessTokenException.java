package com.spid.android.sdk.exceptions;

import java.util.Map;

/**
 * Signals that the access token is invalid or expired
 */
public class SPiDInvalidAccessTokenException extends SPiDException {

    /**
     * Constructs a new SPiDInvalidAccessTokenException with the specified detail message and cause.
     *
     * @param error        The error as a string, see predefined constants in this class
     * @param descriptions The detail messages
     * @param errorCode    The error code
     * @param type         The error type
     */
    public SPiDInvalidAccessTokenException(String error, Map<String, String> descriptions, Integer errorCode, String type) {
        super(error, descriptions, errorCode, type);
    }
}
