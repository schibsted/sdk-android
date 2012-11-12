package com.schibsted.android.sdk.exceptions;

/**
 * Signals that there was a error trying to generate a device fingerprint
 */
public class SPiDDeviceFingerprintException extends SPiDException {

    /**
     * Constructs a new SPiDDeviceFingerprintException with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param throwable The cause
     */
    public SPiDDeviceFingerprintException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
