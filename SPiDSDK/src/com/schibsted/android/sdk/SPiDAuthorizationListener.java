package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.IOException;

/**
 * Listener interface for a SPiD authorization request.
 */
public interface SPiDAuthorizationListener {
    /**
     * Called when the authorization has been successfully completed
     */
    public void onComplete();

    /**
     * Called when there is a SPiDException
     *
     * @param exception The SPiDException
     */
    public void onSPiDException(SPiDException exception);

    /**
     * Called when there is a IOException, i.e. connection problems
     *
     * @param exception The IOException
     */
    public void onIOException(IOException exception);

    /**
     * Called when there is a Exception which is not handled
     *
     * @param exception The Exception
     */
    public void onException(Exception exception);
}
