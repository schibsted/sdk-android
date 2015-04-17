package com.spid.android.sdk.listener;

/**
 * Listener interface for a SPiD authorization request.
 */
public interface SPiDAuthorizationListener {

    /**
     * Called when the authorization has been successfully completed
     */
    public void onComplete();

    /**
     * Called when there is a Exception which is not handled
     *
     * @param exception The Exception
     */
    public void onError(Exception exception);
}
