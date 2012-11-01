package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 11:52 AM
 */
public class SPiDOauthException extends SPiDException {

    public SPiDOauthException(String msg) {
        super(msg);
    }

    public SPiDOauthException(Throwable throwable) {
        super(throwable);
    }

    public SPiDOauthException(String msg, Throwable t) {
        super(msg, t);
    }

    public SPiDOauthException(String error, String description, Integer errorCode, String type) {
        super(error, description, errorCode, type);
    }
}
