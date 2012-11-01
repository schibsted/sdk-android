package com.schibsted.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import com.schibsted.android.sdk.exceptions.SPiDAuthorizationAlreadyRunningException;

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
    public void authorizationWithBrowser() throws Exception {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDAuthorizationRequest.getAuthorizationURL()));
        getConfig().getContext().startActivity(i);
    }

    public boolean handleIntent(Uri data, SPiDAuthorizationListener listener) {
        if (SPiDAuthorizationRequest.shouldHandleIntent(data)) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        }
        return authorizationRequest.handleIntent(data);
    }

    // Webview
    public WebView getAuthorizationWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getAuthorizationWebView(context, webView);
    }

    public WebView getAuthorizationWebView(Context context, SPiDAuthorizationListener listener) throws Exception {
        return getAuthorizationWebView(context, null, listener);
    }

    public WebView getRegistrationWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getRegistrationWebView(context, webView);
    }

    public WebView getRegistrationWebView(Context context, SPiDAuthorizationListener listener) throws Exception {
        return getRegistrationWebView(context, null, listener);
    }

    public WebView getLostPasswordWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws Exception {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getLostPasswordWebView(context, webView);
    }

    public WebView getLostPasswordWebView(Context context, SPiDAuthorizationListener listener) throws Exception {
        return getLostPasswordWebView(context, null, listener);
    }

    // Refresh token
    public void refreshAccessToken(SPiDAuthorizationListener listener) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
            authorizationRequest.refreshAccessToken(token.getRefreshToken());
        } else {
            if (listener != null)
                listener.onSPiDException(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
        }
    }

    // Logout
    public void logoutSPiDAPI(SPiDAuthorizationListener listener) {
        if (token != null) {
            if (authorizationRequest == null) {
                authorizationRequest = new SPiDAuthorizationRequest(listener);
                authorizationRequest.softLogout(token);
            } else {
                listener.onSPiDException(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
            }
        } else {
            listener.onComplete();
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
    public SPiDRequest apiGetRequest(String path, SPiDRequestListener listener) {
        SPiDRequest request = new SPiDRequest("GET", config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
        request.addQueryParameter("oauth_token", token.getAccessToken());
        return request;
    }

    public SPiDRequest apiPostRequest(String path, SPiDRequestListener listener) {
        SPiDRequest request = new SPiDRequest("POST", config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
        request.addBodyParameter("oauth_token", token.getAccessToken());
        return request;
    }

    // Request wrappers
    public void getOneTimeCode(SPiDRequestListener listener) {
        SPiDRequest request = apiPostRequest("/oauth/exchange", listener);
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("client_id", config.getServerClientID());
        request.addBodyParameter("type", "code");
        request.execute();
    }

    public void getCurrentUser(SPiDRequestListener listener) {
        SPiDRequest request = apiGetRequest("/user/" + token.getUserID(), listener);
        request.execute();
    }

    // Protected methods
    protected void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    protected void runWaitingRequests() {
        List<SPiDRequest> requests = new ArrayList<SPiDRequest>(waitingRequests);
        waitingRequests.clear();

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
        waitingRequests.add(request);
    }

    public void clearAuthorizationRequest() {
        authorizationRequest = null;
    }
}

