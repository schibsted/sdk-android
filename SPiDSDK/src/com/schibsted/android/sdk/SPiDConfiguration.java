package com.schibsted.android.sdk;

import android.content.Context;

/**
 * Contains a configuration for the SPiD SDK
 */
public class SPiDConfiguration {

    private String clientID;
    private String clientSecret;
    private String appURLScheme;
    private String serverURL;
    private String authorizationURL;
    private String registrationURL;
    private String lostPasswordURL;
    private String tokenURL;
    private String redirectURL;
    private String serverClientID;
    private Boolean useMobileWeb;
    private String apiVersion;
    private Context context;

    /**
     * Constructor for SPiDConfiguration object.
     *
     * @param clientID         SPiD client id
     * @param clientSecret     SPiD client secret
     * @param appURLScheme     Android app url scheme
     * @param serverURL        SPiD server url
     * @param redirectURL      SPiD redirect url
     * @param authorizationURL SPiD authorization url
     * @param registrationURL  SPiD registration url
     * @param lostPasswordURL  SPiD lost password url
     * @param tokenURL         SPiD token url
     * @param serverClientID   SPiD client id for server
     * @param useMobileWeb     Use mobile flag
     * @param apiVersion       SPiD API version
     * @param context          Android application context
     */
    protected SPiDConfiguration(String clientID, String clientSecret, String appURLScheme, String serverURL, String redirectURL, String authorizationURL, String registrationURL, String lostPasswordURL, String tokenURL, String serverClientID, Boolean useMobileWeb, String apiVersion, Context context) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.appURLScheme = appURLScheme;
        this.serverURL = serverURL;
        this.redirectURL = redirectURL;
        this.authorizationURL = authorizationURL;
        this.registrationURL = registrationURL;
        this.lostPasswordURL = lostPasswordURL;
        this.tokenURL = tokenURL;
        this.serverClientID = serverClientID;
        this.useMobileWeb = useMobileWeb;
        this.apiVersion = apiVersion;
        this.context = context;
    }

    /**
     * @return SPiD client id
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @param clientID SPiD client id
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * @return SPiD client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret SPiD client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return Android app url scheme
     */
    public String getAppURLScheme() {
        return appURLScheme;
    }

    /**
     * @param appURLScheme Android app url scheme
     */
    public void setAppURLScheme(String appURLScheme) {
        this.appURLScheme = appURLScheme;
    }

    /**
     * @return SPiD server url
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * @param serverURL SPiD server url
     */
    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    /**
     * @return SPiD redirect url, default value: <code>getAppURLScheme() + "://";</code>
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * @param redirectURL SPiD redirect url
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    /**
     * @return SPiD authorization url, default value: <code>getServerURL() + "/auth/login";</code>
     */
    public String getAuthorizationURL() {
        return authorizationURL;
    }

    /**
     * @param authorizationURL SPiD authorization url
     */
    public void setAuthorizationURL(String authorizationURL) {
        this.authorizationURL = authorizationURL;
    }

    /**
     * @return SPiD registration url, default value: <code>getServerURL() + "/auth/signup";</code>
     */
    public String getRegistrationURL() {
        return registrationURL;
    }

    /**
     * @param registrationURL SPiD registration url
     */
    public void setRegistrationURL(String registrationURL) {
        this.registrationURL = registrationURL;
    }

    /**
     * @return SPiD lost password url, default value: <code>getServerURL() + "/auth/forgotpassword";</code>
     */
    public String getLostPasswordURL() {
        return lostPasswordURL;
    }

    /**
     * @param lostPasswordURL SPiD lost password url
     */
    public void setLostPasswordURL(String lostPasswordURL) {
        this.lostPasswordURL = lostPasswordURL;
    }

    /**
     * @return SPiD token url, default value: <code>getServerURL() + "/oauth/token";</code>
     */
    public String getTokenURL() {
        return tokenURL;
    }

    /**
     * @param tokenURL SPiD token url
     */
    public void setTokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
    }

    /**
     * @return SPiD API version, default 2
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @param apiVersion SPiD API version
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * @return SPiD client id for server, used for one time code. Default value: <code>getClientId()</code>
     */
    public String getServerClientID() {
        return serverClientID;
    }

    /**
     * @param serverClientID SPiD client id for server
     */
    public void setServerClientID(String serverClientID) {
        this.serverClientID = serverClientID;
    }

    /**
     * @return If use mobile flag should be set, default value: <code>true</code>
     */
    public Boolean getUseMobileWeb() {
        return useMobileWeb;
    }

    /**
     * @param useMobileWeb Use mobile flag
     */
    public void setUseMobileWeb(Boolean useMobileWeb) {
        this.useMobileWeb = useMobileWeb;
    }

    /**
     * @return Android application context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param context Android application context
     */
    public void setContext(Context context) {
        this.context = context;
    }
}
