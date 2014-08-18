package com.spid.android.sdk.request;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;

/**
 * Contains a user credential access token request to SPiD
 */
public class SPiDUserCredentialTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor
     *
     * @param username              Username
     * @param password              Password
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDUserCredentialTokenRequest(String username, String password, SPiDAuthorizationListener authorizationListener) {
        super(authorizationListener);

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        this.addBodyParameter("grant_type", "password");
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("username", username);
        this.addBodyParameter("password", password);
    }
}
