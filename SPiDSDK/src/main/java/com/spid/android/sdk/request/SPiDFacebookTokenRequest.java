package com.spid.android.sdk.request;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.jwt.SPiDJwt;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;

import java.util.Date;

/**
 * Contains a facebook access token request to SPiD
 */
public class SPiDFacebookTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor for the SPiDUserCredentialTokenRequest
     *
     * @param appId                 Facebook app id
     * @param expiration            Facebook token expiration
     * @param facebookToken         Facebook token
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDFacebookTokenRequest(String appId, String facebookToken, Date expiration, SPiDAuthorizationListener authorizationListener) throws SPiDException {
        super(authorizationListener);

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDJwt jwt = new SPiDJwt(appId, "authorization", config.getTokenURL(), expiration, "facebook", facebookToken);
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        this.addBodyParameter("assertion", jwt.encodedJwtString());
    }
}