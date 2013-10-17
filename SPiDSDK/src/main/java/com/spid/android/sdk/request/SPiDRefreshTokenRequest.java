package com.spid.android.sdk.request;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;

/**
 * Contains a token refresh request to SPiD
 */
public class SPiDRefreshTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor for the SPiDTokenRequest
     *
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDRefreshTokenRequest(SPiDAuthorizationListener authorizationListener) {
        super(authorizationListener);

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String refreshToken = SPiDClient.getInstance().getAccessToken().getRefreshToken();
        this.addBodyParameter("grant_type", "refresh_token");
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("refresh_token", refreshToken);
        this.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
    }
}
