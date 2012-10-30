package com.schibsted.android.sdk;

import org.json.JSONException;

import java.io.EOFException;

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
    protected void doOnPostExecute(SPiDResponse result) {
        if (result != null) {
            try {
                if ((result.getJsonObject().has("error")) && !(result.getJsonObject().getString("error").equals("null"))) {
                    // Error requesting token
                    callback.onError(new EOFException());
                } else {
                    callback.onComplete(result);
                }
            } catch (JSONException e) {
                callback.onError(new EOFException());
            }
            return;
        }
        callback.onError(new EOFException());
    }
}
