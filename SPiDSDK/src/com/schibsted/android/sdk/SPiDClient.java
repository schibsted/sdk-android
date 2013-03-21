package com.schibsted.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import com.schibsted.android.sdk.exceptions.SPiDAuthorizationAlreadyRunningException;
import com.schibsted.android.sdk.keychain.SPiDKeychain;
import com.schibsted.android.sdk.request.SPiDAuthorizationRequest;
import com.schibsted.android.sdk.request.SPiDRequest;
import com.schibsted.android.sdk.webview.SPiDWebViewClient;

import java.io.UnsupportedEncodingException;
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

    /**
     * Constructor for SPiDClient, private since class is a singleton and should always be accessed through <code>getInstance()</code>
     */
    private SPiDClient() {
        config = null;
        token = null;
        authorizationRequest = null;
        waitingRequests = new ArrayList<SPiDRequest>();
    }

    /**
     * Singleton method for SPiDClient which returns the SPiDClient instance, creates a new instance if it does not exist
     *
     * @return SPiDClient instance
     */
    public static SPiDClient getInstance() {
        return instance;
    }

    /**
     * Configures the SPiDClient, this should be the first method called on the SPiDClient
     *
     * @param config Configuration for SPiD
     */
    public void configure(SPiDConfiguration config) {
        this.config = config;
        token = SPiDKeychain.decryptAccessTokenFromSharedPreferences(config.getContext(), config.getClientSecret());
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     * @throws UnsupportedEncodingException
     */
    public void authorizationWithBrowser() throws UnsupportedEncodingException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDAuthorizationRequest.getAuthorizationURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Handles Intents received from SPiD
     *
     * @param data     Intent data
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return <code>true</code> if <code>Intent</code> was handled otherwise <code>false</code>
     */
    public boolean handleIntent(Uri data, SPiDAuthorizationListener listener) {
        if (SPiDAuthorizationRequest.shouldHandleIntent(data)) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        }
        return authorizationRequest.handleIntent(data);
    }

    /**
     * Sets up a WebView with SPiD login
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @param listener      Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getAuthorizationWebView(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getAuthorizationWebView(context, webView, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD login
     *
     * @param context  Android application context
     * @param webView  WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getAuthorizationWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getAuthorizationWebView(context, webView, null, listener);
    }

    /**
     * Sets up a WebView with SPiD login
     *
     * @param context  Android application context
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getAuthorizationWebView(Context context, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getAuthorizationWebView(context, null, listener);
    }

    /**
     * Sets up a WebView with SPiD registration
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD registration, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @param listener      Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getRegistrationWebView(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getRegistrationWebView(context, webView, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD registration
     *
     * @param context  Android application context
     * @param webView  WebView that should be instantiated to SPiD registration, creates a new WebView if <code>null</code>
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getRegistrationWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getRegistrationWebView(context, webView, null, listener);
    }

    /**
     * Sets up a WebView with SPiD registration
     *
     * @param context  Android application context
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getRegistrationWebView(Context context, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getRegistrationWebView(context, null, listener);
    }

    /**
     * Sets up a WebView with SPiD lost password
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD lost password, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @param listener      Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getLostPasswordWebView(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return authorizationRequest.getLostPasswordWebView(context, webView, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD lost password
     *
     * @param context  Android application context
     * @param webView  WebView that should be instantiated to SPiD lost password, creates a new WebView if <code>null</code>
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getLostPasswordWebView(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getLostPasswordWebView(context, webView, null, listener);
    }

    /**
     * Sets up a WebView with SPiD lost password
     *
     * @param context  Android application context
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public WebView getLostPasswordWebView(Context context, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return getLostPasswordWebView(context, null, listener);
    }

    /**
     * Requests access token from SPiD using previously obtained code
     *
     * @param code Refresh token previously from SPiD
     */
    public void requestAccessToken(String code) {
        if (authorizationRequest != null) {
            authorizationRequest.requestAccessToken(code);
        }
    }

    /**
     * Requests a new access token using the refresh token
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void refreshAccessToken(SPiDAuthorizationListener listener) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(listener);
            authorizationRequest.refreshAccessToken(token.getRefreshToken());
        } else {
            if (listener != null)
                listener.onSPiDException(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
        }
    }
/**
 * Logout from SPiD without redirect to Safari, therefor any existing cookie will not be removed
 *
 * @param token Token to logout
 */

    /**
     * Redirects to browser for logout
     *
     * @throws UnsupportedEncodingException
     */
    public void logoutSPiDBrowser() throws UnsupportedEncodingException {
        if (token != null) {
            if (authorizationRequest == null) {
                authorizationRequest = new SPiDAuthorizationRequest(null);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDAuthorizationRequest.getLogoutURL(token)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getConfig().getContext().startActivity(intent);
            } else {
                throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
            }
        }
    }

    /**
     * Logout from SPiD without redirect to Safari, therefor any existing cookie will not be removed
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void logoutSPiDAPI(SPiDAuthorizationListener listener) {
        if (token != null) {
            if (authorizationRequest == null) {
                authorizationRequest = new SPiDAuthorizationRequest(listener);
                authorizationRequest.apiLogout(token);
            } else {
                if (listener != null)
                    listener.onSPiDException(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
            }
        } else {
            if (listener != null)
                listener.onComplete();
        }
    }

    /**
     * @return Access token expiry date of <code>null</code> if there is no access token
     */
    public Date getTokenExpiresAt() {
        if (token != null)
            return token.getExpiresAt();
        return null;
    }

    /**
     * @return <code>true</code> if there is a access token otherwise <code>false</code>
     */
    public boolean isAuthorized() {
        return token != null;
    }

    /**
     * @return Current configuration
     */
    public SPiDConfiguration getConfig() {
        return config;
    }

    /**
     * @return Access token
     */
    public SPiDAccessToken getAccessToken() {
        return token;
    }

    /**
     * Creates a GET API request to SPiD
     *
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The SPiDRequest
     */
    public SPiDRequest apiGetRequest(String path, SPiDRequestListener listener) {
        SPiDRequest request = new SPiDRequest("GET", config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
        request.addQueryParameter("oauth_token", token.getAccessToken());
        return request;
    }

    /**
     * Creates a POST API request to SPiD
     *
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The request
     */
    public SPiDRequest apiPostRequest(String path, SPiDRequestListener listener) {
        SPiDRequest request = new SPiDRequest("POST", config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
        request.addBodyParameter("oauth_token", token.getAccessToken());
        return request;
    }

    /**
     * Request wrapper to getting one time code
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getOneTimeCode(SPiDRequestListener listener) {
        SPiDRequest request = apiPostRequest("/oauth/exchange", listener);
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("client_id", config.getServerClientID());
        request.addBodyParameter("type", "code");
        request.execute();
    }

    /**
     * Request wrapper to get current user, e.g. /user/123
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getCurrentUser(SPiDRequestListener listener) {
        SPiDRequest request = apiGetRequest("/user/" + token.getUserID(), listener);
        request.execute();
    }

    /**
     * @param accessToken Current access token
     */
    public void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    /**
     * Runs requests that have been on hold during authentication
     */
    public void runWaitingRequests() {
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

    /**
     * Clears current access token and remove all waiting requests
     */
    public void clearAccessTokenAndWaitingRequests() {
        clearAccessToken();
        waitingRequests.clear();
    }

    /**
     * Clears current access token for SPiDClient and SharedPreferences
     */
    public void clearAccessToken() {
        token = null;
        SPiDKeychain.clearAccessTokenFromSharedPreferences(config.getContext());
    }

    /**
     * Adds a request to the waiting queue, this will be run when valid access token has been received
     *
     * @param request The request to be added
     */
    public void addWaitingRequest(SPiDRequest request) {
        waitingRequests.add(request);
    }

    /**
     * Clears current authorization request
     */
    public void clearAuthorizationRequest() {
        authorizationRequest = null;
    }

    public boolean getDebug() {
        return config.getDebugMode();
    }
}

