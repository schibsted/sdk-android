package com.schibsted.android.sdk.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SPiDApiException extends SPiDException {
    public SPiDApiException(String errorMessage) {
        super(errorMessage);
    }

    public SPiDApiException(String error, String description, Integer errorCode, String type) {
        super(error, description, errorCode, type);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
