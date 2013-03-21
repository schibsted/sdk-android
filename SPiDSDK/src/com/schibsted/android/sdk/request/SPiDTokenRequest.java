package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.*;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.keychain.SPiDKeychain;
import com.schibsted.android.sdk.reponse.SPiDResponse;

import java.io.IOException;

/**
 * Contains a access token request to SPiD
 */
public class SPiDTokenRequest extends SPiDRequest {
    /**
     * Constructor for the SPiDTokenRequest
     *
     * @param listener Called on completion or error, can be <code>null</code>
     */
    public SPiDTokenRequest(SPiDRequestListener listener) {
        super(SPiDRequest.POST, SPiDClient.getInstance().getConfig().getTokenURL(), listener);
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
            } else {
                if (listener != null)
                    listener.onException(exception);
            }
        } else {
            SPiDAccessToken token = new SPiDAccessToken(response.getJsonObject());
            SPiDClient.getInstance().setAccessToken(token);
            SPiDKeychain.encryptAccessTokenToSharedPreferences(SPiDClient.getInstance().getConfig().getContext(), SPiDClient.getInstance().getConfig().getClientSecret(), token);
            SPiDClient.getInstance().runWaitingRequests();
            if (listener != null)
                listener.onComplete(response);
        }
    }
}
