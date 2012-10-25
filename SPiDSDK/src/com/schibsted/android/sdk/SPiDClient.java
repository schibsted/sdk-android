package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private List<SPiDRequest> waitingRequests;

    private SPiDClient() {
        config = null;
        token = null;
        authorizationRequest = null;
        waitingRequests = new ArrayList<SPiDRequest>();
    }

    public static SPiDClient getInstance() {
        return instance;
    }

    public void configure(SPiDConfiguration config) {
        this.config = config;
        token = SPiDKeychain.decryptAccessTokenFromSharedPreferences(config.getContext(), config.getClientSecret());
    }

    // Browser redirect
    public void authorize(SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            // TODO: throw exception, only one authorization request can be running at a single time
        }
    }

    public boolean handleIntent(Uri data) {
        if (authorizationRequest != null) {
            return authorizationRequest.handleIntent(data);
        }
        return false;
    }

    // Webview
    public WebView getAuthorizationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } // TODO: else? clear authorizationRequest

        SPiDLogger.log("Context: " + context.toString() + " url: " + getAuthorizationURL());
        return authorizationRequest.getAuthorizationWebView(context, getAuthorizationURL());
    }

    // Refresh token
    public void refreshAccessToken(SPiDAsyncAuthorizationCallback callback) {
        // TODO!!!
        authorizationRequest = new SPiDAuthorizationRequest(callback);
        authorizationRequest.refreshAccessToken(token.getRefreshToken());
    }

    // Logout
    public void logoutSPiDAPI(SPiDAsyncAuthorizationCallback callback) {
        if (token != null) {
            // TODO: multiple authreq?
            authorizationRequest = new SPiDAuthorizationRequest(callback);
            authorizationRequest.softLogout(token);
        }
    }

    // Properties
    public Date getTokenExpiresAt() {
        if (token != null)
            return token.getExpiresAt();
        return null;
    }

    public boolean isAuthorized() {
        return token != null;
    }

    protected SPiDConfiguration getConfig() {
        return config;
    }

    // Requests
    public SPiDRequest apiGetRequest(String path, SPiDAsyncCallback callback) {
        SPiDRequest request = new SPiDRequest("GET", config.getServerURL() + "/api/" + config.getApiVersion() + path, callback);
        request.addQueryParameter("oauth_token", token.getAccessToken());
        return request;
    }

    public SPiDRequest apiPostRequest(String path, SPiDAsyncCallback callback) {
        SPiDRequest request = new SPiDRequest("POST", config.getServerURL() + "/api/" + config.getApiVersion() + path, callback);
        request.addBodyParameter("oauth_token", token.getAccessToken());
        return request;
    }

    // Request wrappers
    public void getOneTimeCode(SPiDAsyncCallback callback) {
        SPiDRequest request = apiPostRequest("/oauth/exchange", callback);
        request.addBodyParameter("clientId", config.getClientID());
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("type", "code");
        request.execute();
    }

    public void getCurrentUser(SPiDAsyncCallback callback) {
        SPiDRequest request = apiGetRequest("/user/" + token.getUserID(), callback);
        request.execute();
    }

    // Protected methods
    protected void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    protected void runWaitingRequests() {
        SPiDLogger.log("Running waiting requests");
        List<SPiDRequest> requests = new ArrayList<SPiDRequest>(waitingRequests);
        waitingRequests.clear();
        ;

        for (SPiDRequest request : requests) {
            if (request.getMethod().equals("GET")) {
                request.addQueryParameter("oauth_token", token.getAccessToken());
            } else { // POST
                request.addBodyParameter("oauth_token", token.getAccessToken());
            }
            request.execute();
        }
    }

    protected void clearAccessToken() {
        token = null;

        SPiDKeychain.clearAccessTokenFromSharedPreferences(config.getContext());

        waitingRequests.clear();
    }

    protected void addWaitingRequest(SPiDRequest request) {
        SPiDLogger.log("Adding request");
        waitingRequests.add(request);
    }

    // Private methods
    private String getAuthorizationURL() {
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }
}

