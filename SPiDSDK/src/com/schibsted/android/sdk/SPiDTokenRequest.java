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
    public SPiDTokenRequest(String method, String url, SPiDRequestListener listener) {
        super(method, url, listener);
    }

    public SPiDTokenRequest(String url, SPiDRequestListener listener) {
        super(url, listener);
    }

    @Override
    protected void doOnPostExecute(SPiDResponse response) {
        Exception exception = response.getException();
        if (exception != null) {
            if (exception instanceof IOException) {
                listener.onIOException((IOException) exception);
            } else if (exception instanceof SPiDException) {
                listener.onSPiDException((SPiDException) exception);
            }
        } else {
            listener.onComplete(response);
        }
    }
}
