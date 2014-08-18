package com.spid.android.sdk.request;

import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;

/**
 * Contains a code access token request to SPiD
 */
public class SPiDCodeTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor for the SPiDUserCredentialTokenRequest
     *
     * @param code
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDCodeTokenRequest(String code, SPiDAuthorizationListener authorizationListener) {
        super(authorizationListener);

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        this.addBodyParameter("grant_type", "authorization_code");
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("code", code);
        this.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
    }
}