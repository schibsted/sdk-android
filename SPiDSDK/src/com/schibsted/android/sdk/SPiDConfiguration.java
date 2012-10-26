package com.schibsted.android.sdk;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 4:33 PM
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
    private String apiVersion;

    private Context context;

    private String code;

    public SPiDConfiguration(String clientID, String clientSecret, String appURLScheme, String serverURL, String redirectURL, String authorizationURL, String registrationURL, String lostPasswordURL, String tokenURL, String serverClientID, String apiVersion, Context context) {
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
        this.apiVersion = apiVersion;
        this.context = context;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAppURLScheme() {
        return appURLScheme;
    }

    public void setAppURLScheme(String appURLScheme) {
        this.appURLScheme = appURLScheme;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getAuthorizationURL() {
        return authorizationURL;
    }

    public void setAuthorizationURL(String authorizationURL) {
        this.authorizationURL = authorizationURL;
    }

    public String getRegistrationURL() {
        return registrationURL;
    }

    public void setRegistrationURL(String registrationURL) {
        this.registrationURL = registrationURL;
    }

    public String getLostPasswordURL() {
        return lostPasswordURL;
    }

    public void setLostPasswordURL(String lostPasswordURL) {
        this.lostPasswordURL = lostPasswordURL;
    }

    public String getTokenURL() {
        return tokenURL;
    }

    public void setTokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Context getContext() {
        return context;
    }

    public void setServerClientID(String serverClientID) {
        this.serverClientID = serverClientID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getServerClientID() {
        return serverClientID;
    }
}
