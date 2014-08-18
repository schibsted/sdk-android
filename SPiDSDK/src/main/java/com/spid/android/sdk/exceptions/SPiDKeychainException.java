package com.spid.android.sdk.exceptions;

/**
 * Signals that there was a problem encrypting/decrypting the access token from SharedPreferences
 */
public class SPiDKeychainException extends SPiDException {

    /**
     * Constructs a new SPiDKeychainException with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param throwable The cause
     */
    public SPiDKeychainException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
