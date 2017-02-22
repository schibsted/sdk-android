package com.spid.android.sdk;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.exceptions.SPiDAuthorizationAlreadyRunningException;
import com.spid.android.sdk.exceptions.SPiDInvalidResponseException;
import com.spid.android.sdk.keychain.SPiDKeychain;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDApiGetRequest;
import com.spid.android.sdk.request.SPiDApiPostRequest;
import com.spid.android.sdk.request.SPiDCodeTokenRequest;
import com.spid.android.sdk.request.SPiDRefreshTokenRequest;
import com.spid.android.sdk.request.SPiDRequest;
import com.spid.android.sdk.request.SPiDTokenRequest;
import com.spid.android.sdk.response.SPiDResponse;
import com.spid.android.sdk.utils.SPiDUrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class for SPiD, contains a singleton
 */
public class SPiDClient {
    public static final String OAUTH_TOKEN = "oauth_token";

    private static final SPiDClient instance = new SPiDClient();

    private SPiDConfiguration config;
    private SPiDAccessToken token;
    private SPiDAuthorizationListener authorizationListener;
    private final List<SPiDRequest> waitingRequests = new ArrayList<>();

    private enum RequestType {

        CODE("code"),
        SESSION("session");

        private String type;

        RequestType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * Constructor for SPiDClient, private since class is a singleton and should always be accessed through <code>getInstance()</code>
     */
    protected SPiDClient() { }

    /**
     * Singleton method for SPiDClient which returns the SPiDClient instance
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
        setAccessToken(SPiDKeychain.decryptAccessTokenFromSharedPreferences(config.getClientSecret()));
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     */
    public void browserAuthorization() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getAuthorizationURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     */
    public void browserSignup() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getSignupURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getConfig().getContext().startActivity(intent);
    }

    /**
     * Redirects to browser for authorization using a Intent
     *
     */
    public void browserForgotPassword() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getForgotPasswordURL()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
     * @param listener The SPiDAuthorizationListener to handle the callback
     * @return <code>true</code> if <code>Intent</code> was handled otherwise <code>false</code>
     */
    public boolean handleIntent(Uri data, SPiDAuthorizationListener listener) {
        if (shouldHandleIntent(data)) {
            if (data.getPath().endsWith("login")) {
                String code = data.getQueryParameter(RequestType.CODE.toString());
                if (code == null) {
                    if (listener != null) {
                        SPiDLogger.log("User aborted login");
                    }
                    SPiDClient.getInstance().clearAuthorizationRequest();
                } else if (!TextUtils.isEmpty(code)) {
                    SPiDTokenRequest request = new SPiDCodeTokenRequest(code, listener);
                    request.execute();
                    return true;
                } else {
                    if (listener != null) {
                        listener.onError(new SPiDInvalidResponseException("Received invalid code"));
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
                listener.onError(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
        }
    }

    /**
     * Redirects to browser for logout
     *
     */
    public void browserLogout() {
        if (token != null) {
            if (authorizationListener == null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDUrl.getLogoutURL(token)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SPiDClient.getInstance().clearAuthorizationRequest();
                SPiDClient.getInstance().clearAccessTokenAndWaitingRequests();
                getConfig().getContext().startActivity(intent);
            } else {
                throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
            }
        }
    }

    /**
     * Logout from SPiD without redirect to browser, therefore any existing cookie will not be removed
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
                    listener.onError(new SPiDAuthorizationAlreadyRunningException("Authorization already running"));
            }
        } else {
            if (listener != null)
                listener.onComplete();
        }
    }

    /**
     * @return <code>true</code> if there is an access token that has not expired, otherwise <code>false</code>
     */
    public boolean isAuthorized() {
        Date currentMoment = new Date();
        return token != null && currentMoment.before(token.getExpiresAt());
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
     * Sends a local broadcast if the access token is updated using an Intent with the action {@link com.spid.android.sdk.accesstoken.SPiDAccessToken#SPID_ACCESS_TOKEN_EVENT}
     * and the user's id added as a String extra with the key {@link com.spid.android.sdk.accesstoken.SPiDAccessToken#USER_ID}
     *
     * @param accessToken Current access token
     */
    public void setAccessToken(SPiDAccessToken accessToken) {
        if ((token != null && !token.equals(accessToken)) || (accessToken != null && !accessToken.equals(token))) {
            broadcastUserId(accessToken != null ? accessToken.getUserID() : null);
        }
        this.token = accessToken;
    }

    protected void broadcastUserId(String userId) {
        Intent intent = new Intent(SPiDAccessToken.SPID_ACCESS_TOKEN_EVENT);
        intent.putExtra(SPiDAccessToken.USER_ID, userId);
        LocalBroadcastManager.getInstance(getConfig().getContext()).sendBroadcast(intent);
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
        request.addBodyParameter("type", RequestType.CODE.toString());
        request.executeAuthorizedRequest();
    }

    /**
     * Request wrapper to getting session code used for hybrid login
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getSessionCode(SPiDRequestListener listener) {
        if (TextUtils.isEmpty(config.getRedirectURL())) {
            SPiDLogger.log("Redirect URL is necessary and not set, did you forget to set it?");
        }
        SPiDRequest request = new SPiDApiPostRequest("/oauth/exchange", listener);
        request.addBodyParameter("clientId", config.getServerClientID());
        request.addBodyParameter("type", RequestType.SESSION.toString());
        request.addBodyParameter("redirectUri", config.getServerRedirectUri());
        request.executeAuthorizedRequest();
    }

    public void logout(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDRequest(SPiDClient.getInstance().getConfig().getServerURL() + "/logout", listener);
        clearAccessToken();
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
     * Request wrapper to get agreements
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void getAgreements(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDApiGetRequest("/user/" + token.getUserID() + "/agreements", listener);
        request.executeAuthorizedRequest();
    }

    /**
     * Request wrapper to accept both the client and the platform agreement
     *
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public void acceptAgreements(SPiDRequestListener listener) {
        SPiDRequest request = new SPiDApiPostRequest("/user/" + token.getUserID() + "/agreements/accept", listener);
        request.executeAuthorizedRequest();
    }

    /**
     * Runs requests that have been on hold during authentication
     */
    public void runWaitingRequests() {
        List<SPiDRequest> requests = new ArrayList<>(waitingRequests);
        waitingRequests.clear();

        for (SPiDRequest request : requests) {
            if (SPiDRequest.GET.equals(request.getMethod())) {
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
        setAccessToken(null);
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
     *
     * @return the number of requests waiting to be executed
     */
    public int getWaitingRequestsQueueSize() {
        return waitingRequests.size();
    }

    /**
     * Clears current authorization request
     */
    public void clearAuthorizationRequest() {
        authorizationListener = null;
    }

    public boolean isDebug() {
        return config.isDebugMode();
    }

    /**
     * Listener for the logout request
     */
    private class LogoutListener implements SPiDRequestListener {
        private final SPiDAuthorizationListener listener;

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

        public void onError(Exception exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (listener != null)
                listener.onError(exception);
        }
    }
}

