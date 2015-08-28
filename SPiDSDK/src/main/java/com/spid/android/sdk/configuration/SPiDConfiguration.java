package com.spid.android.sdk.configuration;

import android.content.Context;

/**
 * Contains a configuration for the SPiD SDK
 */
public class SPiDConfiguration {

    private final String userAgent;

    private Context context;
    private SPiDEnvironment spidEnvironment;
    private Boolean isDebugMode;

    private String clientID;
    private String clientSecret;
    private String signSecret;
    private String appURLScheme;
    private String authorizationURL;
    private String signupURL;
    private String forgotPasswordURL;
    private String tokenURL;
    private String redirectURL;
    private String serverClientID;
    private String serverRedirectUri;
    private String apiVersion;

    /**
     * Constructor for SPiDConfiguration object.
     *
     * @param clientID          SPiD client id
     * @param clientSecret      SPiD client secret
     * @param signSecret        SPiD sign secret
     * @param appURLScheme      Android app url scheme
     * @param spidEnvironment   SPiD server url
     * @param redirectURL       SPiD redirect url
     * @param authorizationURL  SPiD authorization url
     * @param registrationURL   SPiD registration url
     * @param forgotPasswordURL SPiD lost password url
     * @param tokenURL          SPiD token url
     * @param serverClientID    SPiD client id for server
     * @param serverRedirectUri SPiD redirect uri for server
     * @param apiVersion        SPiD API version
     * @param debugMode         Whether to run in debug mode
     * @param userAgent         SPiD custom User-Agent
     * @param context           Android application context
     */
    protected SPiDConfiguration(String clientID, String clientSecret, String signSecret, String appURLScheme, SPiDEnvironment spidEnvironment, String redirectURL, String authorizationURL, String registrationURL, String forgotPasswordURL, String tokenURL, String serverClientID, String serverRedirectUri, String apiVersion, Boolean debugMode, String userAgent, Context context) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.signSecret = signSecret;
        this.appURLScheme = appURLScheme;
        this.spidEnvironment = spidEnvironment;
        this.redirectURL = redirectURL;
        this.authorizationURL = authorizationURL;
        this.signupURL = registrationURL;
        this.forgotPasswordURL = forgotPasswordURL;
        this.tokenURL = tokenURL;
        this.serverClientID = serverClientID;
        this.serverRedirectUri = serverRedirectUri;
        this.apiVersion = apiVersion;
        this.isDebugMode = debugMode;
        this.userAgent = userAgent;
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
     * @param signSecret SPiD sign secret
     */

    public void setSignSecret(String signSecret) {
        this.signSecret = signSecret;
    }

    /**
     * @return SPiD sign secret
     */
    public String getSignSecret() {
        return signSecret;
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
        return spidEnvironment.toString();
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
     * @return SPiD authorization url, default value: <code>getServerURL() + "/flow/login";</code>
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
     * @return SPiD signup url, default value: <code>getServerURL() + "/auth/signup";</code>
     */
    public String getSignupURL() {
        return signupURL;
    }

    /**
     * @param signupURL SPiD signup url
     */
    public void setSignupURL(String signupURL) {
        this.signupURL = signupURL;
    }

    /**
     * @return SPiD lost password url, default value: <code>getServerURL() + "/flow/forgotpassword";</code>
     */
    public String getForgotPasswordURL() {
        return forgotPasswordURL;
    }

    /**
     * @param forgotPasswordURL SPiD lost password url
     */
    public void setForgotPasswordURL(String forgotPasswordURL) {
        this.forgotPasswordURL = forgotPasswordURL;
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
     * @return SPiD redirect uri for server, used for session code. Default value: <code>getRedirectUri()</code>
     */
    public String getServerRedirectUri() {
        return serverRedirectUri;
    }

    /**
     * @param serverRedirectUri SPiD redirect uri for server
     */
    public void setServerRedirectUri(String serverRedirectUri) {
        this.serverRedirectUri = serverRedirectUri;
    }

    /**
     * @return Use debug mode, default value: <code>false</code>
     */
    public boolean isDebugMode() {
        return isDebugMode;
    }

    /**
     * @param isDebugMode Use debug mode
     */
    public void setDebugMode(Boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    /**
     * @return SPiD custom User-Agent
     */
    public String getUserAgent() {
        return userAgent;
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
