package com.schibsted.android.sdk;

import android.content.Context;

/**
 * Builder class for SPiDConfiguration
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

    /**
     * @param clientID SPiD client id
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder clientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    /**
     * @param clientSecret SPiD client secret
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * @param appURLScheme Android app url scheme
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder appURLScheme(String appURLScheme) {
        this.appURLScheme = appURLScheme;
        return this;
    }

    /**
     * @param serverURL SPiD server url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder serverURL(String serverURL) {
        this.serverURL = serverURL;
        return this;
    }

    /**
     * @param redirectURL SPiD redirect url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder redirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
        return this;
    }

    /**
     * @param authorizationURL SPiD authorization url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder authorizationURL(String authorizationURL) {
        this.authorizationURL = authorizationURL;
        return this;
    }

    /**
     * @param registrationURL SPiD registration url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder registrationURL(String registrationURL) {
        this.registrationURL = registrationURL;
        return this;
    }

    /**
     * @param lostPasswordURL SPiD lost password url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder lostPasswordURL(String lostPasswordURL) {
        this.lostPasswordURL = lostPasswordURL;
        return this;
    }

    /**
     * @param tokenURL SPiD token url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder tokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
        return this;
    }

    /**
     * @param serverClientID SPiD client id for server
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder serverClientID(String serverClientID) {
        this.serverClientID = serverClientID;
        return this;
    }

    /**
     * @param apiVersion SPiD API version
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * @param context Android application context
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder context(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Checks that supplied string is not empty, otherwise throws exception
     *
     * @param string
     * @param errorMessage
     * @throws IllegalArgumentException
     */
    public void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks if supplied object is not null, otherwise throws exception
     *
     * @param object
     * @param errorMessage
     * @throws IllegalArgumentException
     */
    public void isNull(Object object, String errorMessage) {
        if (object == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Builds a SPiDConfiguration object from the supplied values. It also check all that all mandatory values are set and generates default values for non-mandatory values that are missing.
     *
     * @return A SPiDConfiguration object
     */
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

        return new SPiDConfiguration(
                clientID,
                clientSecret,
                appURLScheme,
                serverURL,
                redirectURL,
                authorizationURL,
                registrationURL,
                lostPasswordURL,
                tokenURL,
                serverClientID,
                useMobileWeb,
                apiVersion,
                context);
    }
}
