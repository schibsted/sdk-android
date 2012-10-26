package com.schibsted.android.sdk;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 4:33 PM
 */
public class SPiDConfigurationBuilder {

    private String clientID;
    private String clientSecret;
    private String appURLScheme;
    private String serverURL;
    private String redirectURL;
    private String authorizationURL;
    private String registrationURL;
    private String lostPasswordURL;
    private String tokenURL;
    private String serverClientID;
    private Boolean useMobileWeb = Boolean.TRUE;
    private String apiVersion = "2";
    private Context context;

    public SPiDConfigurationBuilder() {
    }

    public SPiDConfigurationBuilder clientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    public SPiDConfigurationBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public SPiDConfigurationBuilder appURLScheme(String appURLScheme) {
        this.appURLScheme = appURLScheme;
        return this;
    }

    public SPiDConfigurationBuilder serverURL(String serverURL) {
        this.serverURL = serverURL;
        return this;
    }

    public SPiDConfigurationBuilder redirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
        return this;
    }

    public SPiDConfigurationBuilder authorizationURL(String authorizationURL) {
        this.authorizationURL = authorizationURL;
        return this;
    }

    public SPiDConfigurationBuilder registrationURL(String registrationURL) {
        this.registrationURL = registrationURL;
        return this;
    }

    public SPiDConfigurationBuilder lostPasswordURL(String lostPasswordURL) {
        this.lostPasswordURL = lostPasswordURL;
        return this;
    }

    public SPiDConfigurationBuilder tokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
        return this;
    }

    public SPiDConfigurationBuilder serverClientID(String serverClientID) {
        this.serverClientID = serverClientID;
        return this;
    }

    public SPiDConfigurationBuilder apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public SPiDConfigurationBuilder context(Context context) {
        this.context = context;
        return this;
    }


    public void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void isNull(Object object, String errorMessage) {
        if (object == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public SPiDConfiguration build() {
        isEmptyString(clientID, "ClientID is missing");
        isEmptyString(clientSecret, "ClientSecret is missing");
        isEmptyString(appURLScheme, "AppURLScheme is missing");
        isEmptyString(serverURL, "ServerURL is missing");
        isNull(context, "Context is missing");

        if (redirectURL == null || redirectURL.trim().equals("")) {
            redirectURL = appURLScheme + "://";
        }

        if (authorizationURL == null || authorizationURL.trim().equals("")) {
            authorizationURL = serverURL + "/auth/login";
        }

        if (tokenURL == null || tokenURL.trim().equals("")) {
            tokenURL = serverURL + "/oauth/token";
        }

        if (registrationURL == null || registrationURL.trim().equals("")) {
            registrationURL = serverURL + "/auth/signup";
        }

        if (lostPasswordURL == null || lostPasswordURL.trim().equals("")) {
            lostPasswordURL = serverURL + "/auth/forgotpassword";
        }

        if (serverClientID == null || serverClientID.trim().equals("")) {
            serverClientID = clientID;
        }

        return new SPiDConfiguration(clientID, clientSecret, appURLScheme, serverURL, redirectURL, authorizationURL, registrationURL, lostPasswordURL, tokenURL, serverClientID, useMobileWeb, apiVersion, context);
    }
}
