package com.spid.android.sdk.listener;

import com.spid.android.sdk.response.SPiDResponse;

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
     * Called when there is a Exception which is not handled
     *
     * @param exception The Exception
     */
    public void onError(Exception exception);
}
