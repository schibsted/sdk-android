package com.spid.android.sdk.exceptions;

import java.util.Map;

public class SPiDUnverifiedUserException extends SPiDOAuthException {

    /**
     * Constructs a new SPiDUnverifiedUserException with the specified error, description, errorCode and type.
     *
     * @param error        The error as a string, see predefined constants in this class
     * @param descriptions The detail messages
     * @param errorCode    The error code
     * @param type         The error type
     */
    public SPiDUnverifiedUserException(String error, Map<String, String> descriptions, Integer errorCode, String type) {
        super(error, descriptions, errorCode, type);
    }
}
