package com.schibsted.android.sdk.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 1:41 PM
 */
public class SPiDInvalidRequestException extends SPiDException {
    public SPiDInvalidRequestException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
