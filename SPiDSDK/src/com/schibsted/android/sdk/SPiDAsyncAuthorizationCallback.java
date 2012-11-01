package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/12/12
 * Time: 10:19 AM
 */
public interface SPiDAsyncAuthorizationCallback {
    public void onComplete();

    public void onError(SPiDException exception);
}
