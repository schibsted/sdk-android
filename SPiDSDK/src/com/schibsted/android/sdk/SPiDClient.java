package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 4:32 PM
 */
public class SPiDClient {
    private static final SPiDClient instance = new SPiDClient();

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
    public void authorize(SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            throw new Exception("Authorization already running");
            // TODO: throw exception, only one authorization request can be running at a single time
        }
    }

    public boolean handleIntent(Uri data) {
        return authorizationRequest != null && authorizationRequest.handleIntent(data);
    }

    // Webview
    public WebView getAuthorizationWebView(Context context, WebView webView, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            throw new Exception("Authorization already running");
        }

        return authorizationRequest.getAuthorizationWebView(context, webView, authorizationCallback);
    }

    public WebView getAuthorizationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        return getAuthorizationWebView(context, null, authorizationCallback);
    }

    public WebView getRegistrationWebView(Context context, WebView webView, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            throw new Exception("Authorization already running");
        }

        return authorizationRequest.getRegistrationWebView(context, webView, authorizationCallback);
    }

    public WebView getRegistrationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        return getRegistrationWebView(context, null, authorizationCallback);
    }

    public WebView getLostPasswordWebView(Context context, WebView webView, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            throw new Exception("Authorization already running");
        }// TODO: else? clear authorizationRequest

        return authorizationRequest.getLostPasswordWebView(context, webView, authorizationCallback);
    }

    public WebView getLostPasswordWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) throws Exception {
        return getLostPasswordWebView(context, null, authorizationCallback);
    }

    // Refresh token
    public void refreshAccessToken(SPiDAsyncAuthorizationCallback callback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(callback);
            authorizationRequest.refreshAccessToken(token.getRefreshToken());
        } else {
            callback.onError(new Exception("Authorization already running"));
        }
    }

    // Logout
    public void logoutSPiDAPI(SPiDAsyncAuthorizationCallback callback) {
        if (token != null) {
            if (authorizationRequest == null) {
                authorizationRequest = new SPiDAuthorizationRequest(callback);
                authorizationRequest.softLogout(token);
            } else {
                callback.onError(new Exception("Authorization already running"));
            }
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
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("client_id", config.getServerClientID());
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

    protected void clearAuthorizationRequest() {
        authorizationRequest = null;
    }
}

