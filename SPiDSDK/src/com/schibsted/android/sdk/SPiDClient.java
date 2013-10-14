package com.schibsted.android.sdk;

import android.content.Intent;
import android.net.Uri;

import com.schibsted.android.sdk.accesstoken.SPiDAccessToken;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.exceptions.SPiDAuthorizationAlreadyRunningException;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;
import com.schibsted.android.sdk.exceptions.SPiDUserAbortedLoginException;
import com.schibsted.android.sdk.keychain.SPiDKeychain;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.reponse.SPiDResponse;
import com.schibsted.android.sdk.request.SPiDApiGetRequest;
import com.schibsted.android.sdk.request.SPiDApiPostRequest;
import com.schibsted.android.sdk.request.SPiDCodeTokenRequest;
import com.schibsted.android.sdk.request.SPiDRefreshTokenRequest;
import com.schibsted.android.sdk.request.SPiDRequest;
import com.schibsted.android.sdk.request.SPiDTokenRequest;
import com.schibsted.android.sdk.utils.SPiDUrl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class for SPiD, contains a singleton
 */
public class SPiDClient {

    public static final String SPID_ANDROID_SDK_VERSION_STRING = "1.1.3";

    private static final SPiDClient instance = new SPiDClient();

    private SPiDConfiguration config;
    private SPiDAccessToken token;
    private SPiDAuthorizationListener authorizationListener;
    private List<SPiDRequest> waitingRequests;

    /**
     * Constructor for SPiDClient, private since class is a singleton and should always be accessed through <code>getInstance()</code>
     */
    private SPiDClient() {
        config = null;
        token = null;
        authorizationListener = null;
        waitingRequests = new ArrayList<SPiDRequest>();
    }

    /**
     * Singleton method for SPiDClient which returns the SPiDClient instance, creates a new instance if it does not exist
     *
     * @return SPiDClient instance
     */
    public static SPiDClient getInstance() {
        // TODO throw exception if config == null
        return instance;
    }

    /**
     * Configures the SPiDClient, this should be the first method called on the SPiDClient
     *
     * @param config Configuration for SPiD
     */
    public void configure(SPiDConfiguration config) {
        this.config = config;
        token = SPiDKeychain.decryptAccessTokenFromSharedPreferences(config.getClientSecret());
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     * @throws UnsupportedEncodingException
     */
    public void browserAuthorization() throws UnsupportedEncodingException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getAuthorizationURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     * @throws UnsupportedEncodingException
     */
    public void browserSignup() throws UnsupportedEncodingException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getSignupURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     * @throws UnsupportedEncodingException
     */
    public void browserForgotPassword() throws UnsupportedEncodingException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getForgotPasswordURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Checks if the Intent should be handled
     *
     * @param data Intent data
     * @return <code>true</code> if <code>Intent</code> should be handled otherwise <code>false</code>
     */
    public static boolean shouldHandleIntent(Uri data) {
        return data.toString().startsWith(SPiDClient.getInstance().getConfig().getAppURLScheme());
    }

    /**
     * Handles incoming Intent if it is sent from SPiD
     *
     * @param data Intent data
     * @return <code>true</code> if <code>Intent</code> was handled otherwise <code>false</code>
     */
    public boolean handleIntent(Uri data, SPiDAuthorizationListener listener) {
        if (shouldHandleIntent(data)) {
            if (data.getPath().endsWith("login")) {
                String code = data.getQueryParameter("code");
                if (code == null) {
                    if (listener != null) {
                        listener.onSPiDException(new SPiDUserAbortedLoginException("User aborted login"));
                    } else {
                        SPiDLogger.log("User aborted login");
                    }
                    SPiDClient.getInstance().clearAuthorizationRequest();
                } else if (code.length() > 0) {
                    SPiDTokenRequest request = new SPiDCodeTokenRequest(code, listener);
                    request.execute();
                    return true;
                } else {
                    if (listener != null) {
                        listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                    } else {
                        SPiDLogger.log("Received invalid code");
                    }
                    SPiDClient.getInstance().clearAuthorizationRequest();
                }
            }
        }
        return false;
    }

    /**
     * Requests a new access token using the refresh token
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void refreshAccessToken(SPiDAuthorizationListener listener) {
        if (authorizationListener == null) {
            authorizationListener = listener;
            SPiDTokenRequest request = new SPiDRefreshTokenRequest(authorizationListener);
            request.execute();
        } else {
            if (listener != null)
                listener.onSPiDException(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
        }
    }

    /**
     * Redirects to browser for logout
     *
     * @throws UnsupportedEncodingException
     */
    public void browserLogout() throws UnsupportedEncodingException {
        if (token != null) {
            if (authorizationListener == null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getLogoutURL(token)));
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
    public void apiLogout(SPiDAuthorizationListener listener) {
        if (token != null) {
            if (authorizationListener == null) {
                authorizationListener = listener;
                String requestURL = SPiDClient.getInstance().getConfig().getServerURL() + "/logout";
                SPiDRequest request = new SPiDRequest(requestURL, new LogoutListener(authorizationListener));
                request.addQueryParameter("redirect_uri", SPiDClient.getInstance().getConfig().getRedirectURL() + "spid/logout");
                request.addQueryParameter("oauth_token", token.getAccessToken());
                request.setMaxRetryCount(-1);
                request.execute();
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
     * @return <code>true</code> if the access token is a client token <code>false</code>
     */
    public boolean isClientToken() {
        return token != null && token.isClientToken();
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
     * @param accessToken Current access token
     */
    public void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    /**
     * @return Authorization listener
     */
    public SPiDAuthorizationListener getAuthorizationListener() {
        return authorizationListener;
    }

    /**
     * @param authorizationListener Current authorization listener
     */
    public void setAuthorizationListener(SPiDAuthorizationListener authorizationListener) {
        this.authorizationListener = authorizationListener;
    }

    /**
     * Request wrapper to getting one time code
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getOneTimeCode(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDApiPostRequest("/oauth/exchange", listener);
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("client_id", config.getServerClientID());
        request.addBodyParameter("type", "code");
        request.executeAuthorizedRequest();
    }

    /**
     * Request wrapper to getting session code used for hybrid login
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getSessionCode(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDApiPostRequest("/oauth/exchange", listener);
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("type", "session");
        request.addBodyParameter("redirectUri", config.getServerRedirectUri());
        request.executeAuthorizedRequest();
    }

    /**
     * Request wrapper to get current user, e.g. /user/123
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getCurrentUser(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDApiGetRequest("/user/" + token.getUserID(), listener);
        request.executeAuthorizedRequest();
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
        SPiDKeychain.clearAccessTokenFromSharedPreferences();
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
        authorizationListener = null;
    }

    public boolean getDebug() {
        return config.getDebugMode();
    }

    /**
     * Listener for the logout request
     */
    private class LogoutListener implements SPiDRequestListener {
        private SPiDAuthorizationListener listener;

        /**
         * Creates a LogoutListener
         *
         * @param listener Called on completion or error, can be <code>null</code>
         */
        public LogoutListener(SPiDAuthorizationListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public void onComplete(SPiDResponse result) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            SPiDClient.getInstance().clearAccessTokenAndWaitingRequests();
            if (listener != null)
                listener.onComplete();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (listener != null)
                listener.onSPiDException(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (listener != null)
                listener.onIOException(exception);
        }

        public void onException(Exception exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (listener != null)
                listener.onException(exception);
        }
    }
}

