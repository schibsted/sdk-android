package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDAuthorizationListener;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.SPiDConfiguration;
import com.schibsted.android.sdk.SPiDRequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 3/14/13
 * Time: 10:36 AM
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
