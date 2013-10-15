package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;

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
