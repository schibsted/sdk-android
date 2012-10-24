package com.schibsted.android.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/12/12
 * Time: 10:19 AM
 */
public interface SPiDAsyncCallback {
    public void onComplete(SPiDResponse result);

    public void onError(Exception exception);
}
