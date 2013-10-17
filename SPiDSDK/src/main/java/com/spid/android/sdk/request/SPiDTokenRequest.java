package com.spid.android.sdk.request;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.keychain.SPiDKeychain;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.reponse.SPiDResponse;

import java.io.IOException;

/**
 * Contains a access token request to SPiD
 */
public class SPiDTokenRequest extends SPiDRequest {

    private SPiDAuthorizationListener authorizationListener;

    /**
     * Constructor for the SPiDTokenRequest
     *
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDTokenRequest(SPiDAuthorizationListener authorizationListener) {
        super(POST, SPiDClient.getInstance().getConfig().getTokenURL(), null);
        this.authorizationListener = authorizationListener;
    }

    /**
     * Overrides the doOnPostExecute in SPiDRequest since there should not be retires on token requests.
     *
     * @param response The <code>SPiDResponse</code> created in doInBackground
     */
    @Override
    protected void doOnPostExecute(SPiDResponse response) {
        SPiDClient.getInstance().clearAuthorizationRequest();
        Exception exception = response.getException();
        if (exception != null) {
            if (exception instanceof IOException) {
                if (authorizationListener != null)
                    authorizationListener.onIOException((IOException) exception);
            } else if (exception instanceof SPiDException) {
                if (authorizationListener != null)
                    authorizationListener.onSPiDException((SPiDException) exception);
            } else {
                if (authorizationListener != null)
                    authorizationListener.onException(exception);
            }
        } else {
            SPiDAccessToken token = new SPiDAccessToken(response.getJsonObject());
            SPiDClient.getInstance().setAccessToken(token);
            SPiDKeychain.encryptAccessTokenToSharedPreferences(SPiDClient.getInstance().getConfig().getClientSecret(), token);
            SPiDClient.getInstance().runWaitingRequests();
            if (authorizationListener != null)
                authorizationListener.onComplete();
        }
    }
}
