package com.schibsted.android.sdk.exceptions;

/**
 * Signals that the user aborted login
 */
public class SPiDUserAbortedLoginException extends SPiDException {

    /**
     * Constructs a new SPiDUserAbortedLoginException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SPiDUserAbortedLoginException(String message) {
        super(message);
    }
}
