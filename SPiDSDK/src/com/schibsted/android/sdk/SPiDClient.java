package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import java.net.URLEncoder;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 4:32 PM
 */
public class SPiDClient {
    private static final SPiDClient instance = new SPiDClient();

    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";

    private SPiDConfiguration config;

    private SPiDAccessToken token;

    private SPiDAuthorizationRequest authorizationRequest;

    private SPiDClient() {
        config = null;
        authorizationRequest = null;
    }

    public static SPiDClient getInstance() {
        return instance;
    }

    public void configure(SPiDConfiguration config) {
        this.config = config;

        token = SPiDKeychain.decryptAccessTokenFromSharedPreferences(config.getContext(), config.getClientSecret());
    }

    public String getAuthorizationURL() {
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    public WebView getAuthorizationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } // TODO: else? clear authorizationRequest

        SPiDLogger.log("Context: " + context.toString() + " url: " + getAuthorizationURL());
        return authorizationRequest.getAuthorizationWebView(context, getAuthorizationURL());
    }

    public void getCode(Uri data) {
        config.setCode(data.getQueryParameter("code"));
    }

    public void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public boolean handleIntent(Uri data) {
        if (authorizationRequest != null) {
            return authorizationRequest.handleIntent(data);
        }
        return false;
    }

    public void authorize(SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            // TODO: throw exception, only one authorization request can be running at a single time
        }
    }

    public void refreshAccessToken(SPiDAsyncAuthorizationCallback callback) {
        // TODO!!!
        authorizationRequest = new SPiDAuthorizationRequest(callback);
        authorizationRequest.refreshAccessToken(token.getRefreshToken());
    }

    public SPiDConfiguration getConfig() {
        return config;
    }

    public void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    public void apiGetRequest(String path, SPiDAsyncCallback callback) {
        SPiDRequest request = new SPiDRequest("GET", config.getServerURL() + "/api/" + config.getApiVersion() + path, callback);
        request.addQueryParameter("oauth_token", token.getAccessToken());
        request.execute();
    }

    public void getCurrentUser(SPiDAsyncCallback callback) {
        apiGetRequest("/user/" + token.getUserID(), callback);
    }

    public void logoutSPiDAPI(SPiDAsyncCallback callback) {
        SPiDRequest request = new SPiDRequest("POST", "/api/{version}/me", callback);

    }

    public Date getTokenExpiresAt() {
        if (token != null)
            return token.getExpiresAt();
        return null;
    }

    public boolean isAuthorized() {
        return token != null;
    }
}

