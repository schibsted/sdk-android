package com.schibsted.android.sdk.exceptions;

/**
 * Signals that there was a problem with OAuth 2.0, see http://tools.ietf.org/html/draft-ietf-oauth-v2
 */
public class SPiDOAuthException extends SPiDException {

    /**
     * Constructs a new SPiDOAuthException with the specified error, description, errorCode and type.
     *
     * @param error       The error as a string, see predefined constants in this class
     * @param description The detail message
     * @param errorCode   The error code
     * @param type        The error type
     */
    public SPiDOAuthException(String error, String description, Integer errorCode, String type) {
        super(error, description, errorCode, type);
    }
}
