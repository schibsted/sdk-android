package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Contains methods for authorization to SPiD, it handles both WebView and browser redirect.
 */
public class SPiDAuthorizationRequest {
    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";

    private SPiDAuthorizationListener listener;

    /**
     * Constructor for SPiDAuthorizationRequest object.
     *
     * @param authorizationListener Listener called on completion of failure, can be <code>null</code>
     */
    public SPiDAuthorizationRequest(SPiDAuthorizationListener authorizationListener) {
        this.listener = authorizationListener;
    }

    /**
     * Sets up a WebView with SPiD login
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    protected WebView getAuthorizationWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url;
        url = getAuthorizationURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD registration
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    protected WebView getRegistrationWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url;
        url = getRegistrationURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD lost password
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    protected WebView getLostPasswordWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url;
        url = getLostPasswordURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
    }

    /**
     * Sets up a WebView with the provided URL
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param url           URL to open
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     */
    public WebView getWebView(final Context context, WebView webView, String url, SPiDWebViewClient webViewClient) {
        if (SPiDClient.getInstance().isAuthorized()) {
            SPiDLogger.log("Access token found, preforming a soft logout to cleanup before login");
            // Fire and forget
            SPiDAuthorizationRequest authRequest = new SPiDAuthorizationRequest(null);
            authRequest.apiLogout(SPiDClient.getInstance().getAccessToken());
            SPiDClient.getInstance().clearAccessToken();
        }
        if (webView == null) {
            webView = new WebView(context);
        }

        webView.getSettings().setJavaScriptEnabled(true);

        // Fix bug where input never get virtual keyboard focus see: http://code.google.com/p/android/issues/detail?id=7189
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        // This is needed since not all devices do it automatically
        CookieSyncManager.createInstance(context);
        // This is because we do not want to logout through a WebView
        CookieManager.getInstance().removeAllCookie();

        if (webViewClient == null)
            webViewClient = new SPiDWebViewClient();
        webViewClient.setListener(listener);

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);
        return webView;
    }

    /**
     * Requests access token from SPiD using previously obtained code
     *
     * @param code Refresh token previously from SPiD
     */
    protected void requestAccessToken(String code) {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDTokenRequest request = new SPiDTokenRequest("POST", config.getTokenURL(), new AccessTokenListener(listener));
        request.addBodyParameter("grant_type", "authorization_code");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("code", code);
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
        request.execute();
    }

    /**
     * Requests a new access token using the refresh token
     *
     * @param refreshToken Refresh token previously received from SPiD
     */
    public void refreshAccessToken(String refreshToken) {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDRequest request = new SPiDRequest("POST", config.getTokenURL(), new AccessTokenListener(listener));
        request.addBodyParameter("grant_type", "refresh_token");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("refresh_token", refreshToken);
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
        request.execute();
    }

    /**
     * Checks if the Intent should be handled by the SPiDAuthorizationRequest
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
     * @return <code>true</code> if <code>Intent</code> should be handled otherwise <code>false</code>
     */
    public boolean handleIntent(Uri data) {
        if (shouldHandleIntent(data)) {
            if (data.getPath().endsWith("login")) {
                String code = data.getQueryParameter("code");
                if (code.length() > 0) {
                    requestAccessToken(code);
                    return true;
                } else {
                    if (listener != null)
                        listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                    else
                        SPiDLogger.log("Received invalid code");
                }
            }
        }
        return false;
    }

    /**
     * Logout from SPiD without redirect to Safari, therefor any existing cookie will not be removed
     *
     * @param token Token to logout
     */
    public void apiLogout(SPiDAccessToken token) {
        String requestURL = SPiDClient.getInstance().getConfig().getServerURL() + "/logout";
        SPiDRequest request = new SPiDRequest(requestURL, new LogoutListener(listener));
        request.addQueryParameter("redirect_uri", SPiDClient.getInstance().getConfig().getRedirectURL() + "spid/logout");
        request.addQueryParameter("oauth_token", token.getAccessToken());
        request.execute();
    }

    /**
     * Generates URL for authorization in SPiD
     *
     * @return URL for authorization
     * @throws UnsupportedEncodingException
     */
    protected static String getAuthorizationURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for registration in SPiD
     *
     * @return URL for registration
     * @throws UnsupportedEncodingException
     */
    protected static String getRegistrationURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getRegistrationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for lost password in SPiD
     *
     * @return URL for lost password
     * @throws UnsupportedEncodingException
     */
    protected static String getLostPasswordURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getLostPasswordURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }


    /**
     * Generates URL for logout in SPiD
     *
     * @param accessToken Access token to logout
     * @return URL for logout
     * @throws UnsupportedEncodingException
     */
    protected static String getLogoutURL(SPiDAccessToken accessToken) throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String requestURL = SPiDClient.getInstance().getConfig().getServerURL() + "/logout";
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return requestURL + "?redirect_uri=" + encodedRedirectURL + "&oauth_token=" + accessToken.getAccessToken();
    }

    /**
     * Listener for the access token request
     */
    private class AccessTokenListener implements SPiDRequestListener {
        private SPiDAuthorizationListener listener;

        /**
         * Creates a AccessTokenListener
         *
         * @param listener Called on completion or error, can be <code>null</code>
         */
        public AccessTokenListener(SPiDAuthorizationListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public void onComplete(SPiDResponse response) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            Exception exception = response.getException();
            if (exception != null) {
                if (exception instanceof IOException) {
                    if (listener != null)
                        listener.onIOException((IOException) exception);
                    else
                        SPiDLogger.log("Received IOException: " + exception.getMessage());
                } else if (exception instanceof SPiDException) {
                    if (listener != null)
                        listener.onSPiDException((SPiDException) exception);
                    else
                        SPiDLogger.log("Received IOException: " + exception.getMessage());
                } else {
                    SPiDLogger.log("Received unknown exception: " + exception.getMessage());
                }
            } else {
                SPiDAccessToken token = new SPiDAccessToken(response.getJsonObject());
                SPiDClient.getInstance().setAccessToken(token);
                SPiDKeychain.encryptAccessTokenToSharedPreferences(SPiDClient.getInstance().getConfig().getContext(), SPiDClient.getInstance().getConfig().getClientSecret(), token);
                SPiDClient.getInstance().runWaitingRequests();
                if (listener != null)
                    listener.onComplete();
            }
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
    }
}
