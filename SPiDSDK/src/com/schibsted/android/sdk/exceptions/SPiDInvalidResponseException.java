package com.schibsted.android.sdk.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SPiDInvalidResponseException extends SPiDException {
    public SPiDInvalidResponseException(String msg) {
        super(msg);
    }

    public SPiDInvalidResponseException(Throwable throwable) {
        super(throwable);
    }

    public SPiDInvalidResponseException(String msg, Throwable t) {
        super(msg, t);
    }
}
