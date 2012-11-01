package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 9:20 PM
 */
public class SPiDAuthorizationRequest {
    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";

    private SPiDAsyncAuthorizationCallback callback;

    public SPiDAuthorizationRequest(SPiDAsyncAuthorizationCallback authorizationCallback) {
        this.callback = authorizationCallback;
    }

    protected WebView getAuthorizationWebView(Context context, WebView webView) throws UnsupportedEncodingException {
        String url = null;
        url = getAuthorizationURL().concat("&webview=1");
        return getWebView(context, webView, url);
    }

    protected WebView getRegistrationWebView(Context context, WebView webView) throws UnsupportedEncodingException {
        String url = null;
        url = getRegistrationURL().concat("&webview=1");
        return getWebView(context, webView, url);
    }

    protected WebView getLostPasswordWebView(Context context, WebView webView) throws UnsupportedEncodingException {
        String url = null;
        url = getLostPasswordURL().concat("&webview=1");
        return getWebView(context, webView, url);
    }

    public WebView getWebView(final Context context, WebView webView, String url) {
        if (webView == null) {
            webView = new WebView(context);
        }

        webView.getSettings().setJavaScriptEnabled(true);

        // This is because we do not want to logout through a webview
        CookieManager.getInstance().removeAllCookie();

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(context, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                SPiDLogger.log("Opening URL: " + url);
                Uri uri = Uri.parse(url);
                if (url.startsWith(SPiDClient.getInstance().getConfig().getAppURLScheme())) {
                    if (uri.getPath().endsWith("login")) {
                        String code = uri.getQueryParameter("code");
                        if (code.length() > 0) {
                            getAccessToken(code);
                        } else {
                            if (callback != null)
                                callback.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                            else
                                SPiDLogger.log("Received invalid code");
                        }
                        return true;
                    } else if (uri.getPath().endsWith("failure")) {
                        if (callback != null)
                            callback.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                        else
                            SPiDLogger.log("Received invalid code");
                    }

                } else {
                    view.loadUrl(url);
                }
                return false;
            }
        });
        webView.loadUrl(url);
        return webView;
    }

    public void getAccessToken(String code) {
        //isEmptyString(config.getCode(), "No code available");
        // TODO: prevent multiple calls

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDTokenRequest request = new SPiDTokenRequest("POST", config.getTokenURL(), new AccessTokenCallback(callback));
        request.addBodyParameter("grant_type", "authorization_code");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("code", code);
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
        request.execute();
    }

    public void refreshAccessToken(String refreshToken) {
        //isEmptyString(config.getCode(), "No code available");
        // TODO: prevent multiple calls

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDRequest request = new SPiDRequest("POST", config.getTokenURL(), new AccessTokenCallback(callback));
        request.addBodyParameter("grant_type", "refresh_token");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("refresh_token", refreshToken);
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "spid/login");
        request.execute();
    }

    public boolean handleIntent(Uri data) {
        if (data.toString().startsWith(SPiDClient.getInstance().getConfig().getAppURLScheme())) {
            if (data.getPath().endsWith("login")) {
                String code = data.getQueryParameter("code");
                if (code.length() > 0) {
                    getAccessToken(code);
                    return true;
                } else {
                    if (callback != null)
                        callback.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                    else
                        SPiDLogger.log("Received invalid code");
                }
            }
        }
        return false;
    }

    public void softLogout(SPiDAccessToken token) {
        String requestURL = SPiDClient.getInstance().getConfig().getServerURL() + "/logout";
        SPiDRequest request = new SPiDRequest(requestURL, new LogoutCallback(callback));
        request.addQueryParameter("redirect_uri", SPiDClient.getInstance().getConfig().getRedirectURL() + "spid/logout");
        request.addQueryParameter("oauth_token", token.getAccessToken());
        request.execute();
    }

    // Private methods
    private String getAuthorizationURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    private String getRegistrationURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getRegistrationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    private String getLostPasswordURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getLostPasswordURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    class AccessTokenCallback implements SPiDAsyncCallback {
        private SPiDAsyncAuthorizationCallback callback;

        public AccessTokenCallback(SPiDAsyncAuthorizationCallback callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void onComplete(SPiDResponse response) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            Exception exception = response.getException();
            if (exception != null) {
                if (exception instanceof IOException) {
                    if (callback != null)
                        callback.onIOException((IOException) exception);
                    else
                        SPiDLogger.log("Received IOException: " + exception.getMessage());
                } else if (exception instanceof SPiDException) {
                    if (callback != null)
                        callback.onSPiDException((SPiDException) exception);
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
                if (callback != null)
                    callback.onComplete();
            }
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (callback != null)
                callback.onSPiDException(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (callback != null)
                callback.onIOException(exception);
        }
    }

    class LogoutCallback implements SPiDAsyncCallback {
        private SPiDAsyncAuthorizationCallback callback;

        public LogoutCallback(SPiDAsyncAuthorizationCallback callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void onComplete(SPiDResponse result) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            SPiDClient.getInstance().clearAccessToken();
            if (callback != null)
                callback.onComplete();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (callback != null)
                callback.onSPiDException(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            if (callback != null)
                callback.onIOException(exception);
        }
    }
}
