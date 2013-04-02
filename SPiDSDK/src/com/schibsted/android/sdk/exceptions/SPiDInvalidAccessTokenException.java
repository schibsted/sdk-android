package com.schibsted.android.sdk.exceptions;

import java.util.Map;

/**
 * Signals that the access token is invalid or expired
 */
public class SPiDInvalidAccessTokenException extends SPiDException {

    /**
     * Constructs a new SPiDInvalidAccessTokenException with the specified detail message and cause.
     *
     * @param error
     * @param descriptions
     * @param errorCode
     * @param type
     */
    public SPiDInvalidAccessTokenException(String error, Map<String, String> descriptions, Integer errorCode, String type) {
        super(error, descriptions, errorCode, type);
    }
}
