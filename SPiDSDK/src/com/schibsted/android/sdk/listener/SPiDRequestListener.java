package com.schibsted.android.sdk.listener;

import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.reponse.SPiDResponse;

import java.io.IOException;

/**
 * Listener interface for a SPiD request.
 */
public interface SPiDRequestListener {
    /**
     * Called when the authorization has been successfully completed
     *
     * @param result The SPiDResponse
     */
    public void onComplete(SPiDResponse result);

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
