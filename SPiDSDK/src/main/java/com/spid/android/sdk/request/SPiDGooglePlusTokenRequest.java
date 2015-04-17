package com.spid.android.sdk.request;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.TokenType;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.jwt.SPiDJwt;
import com.spid.android.sdk.jwt.SubjectClaim;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains a Google+ access token request to SPiD
 */
public class SPiDGooglePlusTokenRequest extends SPiDTokenRequest {
    /**
     * Constructor for the SPiDUserCredentialTokenRequest
     *
     * @param packageId             Android package id
     * @param googlePlusToken       Google+ token
     * @param authorizationListener Called on completion or error, can be <code>null</code>
     */
    public SPiDGooglePlusTokenRequest(String packageId, String googlePlusToken, SPiDAuthorizationListener authorizationListener) throws SPiDException {
        super(authorizationListener);

        Date expirationDate = getOneHourInTheFuture();
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDJwt jwt = new SPiDJwt(packageId, SubjectClaim.AUTHORIZATION, config.getTokenURL(), expirationDate, TokenType.GOOGLE_PLUS, googlePlusToken);
        this.addBodyParameter("client_id", config.getClientID());
        this.addBodyParameter("client_secret", config.getClientSecret());
        this.addBodyParameter("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        this.addBodyParameter("assertion", jwt.encodedJwtString());
    }

    /**
     * @return Date one hour in the future
     */
    private static Date getOneHourInTheFuture() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();
    }
}