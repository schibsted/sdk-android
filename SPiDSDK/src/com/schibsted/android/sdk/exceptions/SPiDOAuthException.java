package com.schibsted.android.sdk.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 2:30 PM
 */
public class SPiDOAuthException extends SPiDException {

    public SPiDOAuthException(String msg) {
        super(msg);
    }

    public SPiDOAuthException(Throwable throwable) {
        super(throwable);
    }

    public SPiDOAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public SPiDOAuthException(String error, String description, Integer errorCode, String type) {
        super(error, description, errorCode, type);
    }
}
