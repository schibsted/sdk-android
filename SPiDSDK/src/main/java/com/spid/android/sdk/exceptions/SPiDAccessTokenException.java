package com.spid.android.sdk.exceptions;

/**
 * Signals that there was a problem generating a SPiDAccessToken
 */
public class SPiDAccessTokenException extends SPiDException {

    /**
     * Constructs a new SPiDAccessTokenException with the specified detail message and cause.
     *
     * @param message The detail message
     */
    public SPiDAccessTokenException(String message) {
        super(message);
    }
}
