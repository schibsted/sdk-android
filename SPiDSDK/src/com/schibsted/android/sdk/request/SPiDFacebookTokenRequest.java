package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.jwt.SPiDJwt;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;

import java.util.Date;

/**
 * Contains a code access token request to SPiD
 */
public class SPiDFacebookTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor for the SPiDUserCredentialTokenRequest
     *
     * @param
     * @param
     * @param
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDFacebookTokenRequest(String appId, String facebookToken, Date expiration, SPiDAuthorizationListener authorizationListener) {
        super(authorizationListener);

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        try {
            SPiDJwt jwt = new SPiDJwt(appId, "authorization", config.getTokenURL(), expiration, "facebook", facebookToken);
            this.addBodyParameter("assertion", jwt.encodedJwtString());
        } catch (Exception e) {
            // TODO: should have another exception
            authorizationListener.onSPiDException(new SPiDException("Could not create JWT from facebook token"));
        }
    }
}