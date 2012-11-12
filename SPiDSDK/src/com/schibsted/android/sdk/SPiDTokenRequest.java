package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.IOException;

/**
 * Contains a access token request to SPiD
 */
public class SPiDTokenRequest extends SPiDRequest {
    /**
     * Constructor for the SPiDTokenRequest
     *
     * @param method   The http method to be used
     * @param url      The request url
     * @param listener Called on completion or error, can be <code>null</code>
     */
    public SPiDTokenRequest(String method, String url, SPiDRequestListener listener) {
        super(method, url, listener);
    }

    /**
     * Overrides the doOnPostExecute in SPiDRequest since there should not be retires on token requests.
     *
     * @param response The <code>SPiDResponse</code> created in doInBackground
     */
    @Override
    protected void doOnPostExecute(SPiDResponse response) {
        Exception exception = response.getException();
        if (exception != null) {
            if (exception instanceof IOException) {
                if (listener != null)
                    listener.onIOException((IOException) exception);
            } else if (exception instanceof SPiDException) {
                if (listener != null)
                    listener.onSPiDException((SPiDException) exception);
            }
        } else {
            if (listener != null)
                listener.onComplete(response);
        }
    }
}
