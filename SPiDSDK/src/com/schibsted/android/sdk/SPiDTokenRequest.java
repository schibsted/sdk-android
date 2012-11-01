package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/30/12
 * Time: 12:22 PM
 */
public class SPiDTokenRequest extends SPiDRequest {
    public SPiDTokenRequest(String method, String url, SPiDAsyncCallback callback) {
        super(method, url, callback);
    }

    public SPiDTokenRequest(String url, SPiDAsyncCallback callback) {
        super(url, callback);
    }

    @Override
    protected void doOnPostExecute(SPiDResponse response) {
        Exception exception = response.getException();
        if (exception != null) {
            if (exception instanceof IOException) {
                callback.onIOException((IOException) exception);
            } else if (exception instanceof SPiDException) {
                callback.onSPiDException((SPiDException) exception);
            }
        } else {
            callback.onComplete(response);
        }
    }
}
