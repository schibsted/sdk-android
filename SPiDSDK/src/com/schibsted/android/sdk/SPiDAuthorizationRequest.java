package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.json.JSONException;

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

    protected WebView getAuthorizationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        String url = null;
        try {
            url = getAuthorizationURL().concat("&webview=1");
        } catch (UnsupportedEncodingException e) {
            authorizationCallback.onError(e);
        }
        return getWebView(context, url);
    }

    protected WebView getRegistrationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        String url = null;
        try {
            url = getRegistrationURL().concat("&webview=1");
        } catch (UnsupportedEncodingException e) {
            authorizationCallback.onError(e);
        }
        return getWebView(context, url);
    }

    protected WebView getLostPasswordWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        String url = null;
        try {
            url = getLostPasswordURL().concat("&webview=1");
        } catch (UnsupportedEncodingException e) {
            authorizationCallback.onError(e);
        }
        return getWebView(context, url);
    }

    public WebView getWebView(final Context context, String url) {
        WebView webview = new WebView(context);

        webview.getSettings().setJavaScriptEnabled(true);

        // This is because we do not want to logout through a webview
        CookieManager.getInstance().removeAllCookie();

        webview.setWebViewClient(new WebViewClient() {
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
                            callback.onError(new Exception());
                        }
                        return true;
                        //
                    } else if (uri.getPath().endsWith("failure")) {
                        callback.onError(new Exception());
                    }

                } else {
                    view.loadUrl(url);
                }
                return false;
            }
        });
        webview.loadUrl(url);
        return webview;
    }

    public void getAccessToken(String code) {
        //isEmptyString(config.getCode(), "No code available");
        // TODO: prevent multiple calls

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDRequest request = new SPiDRequest("POST", config.getTokenURL(), new AccessTokenCallback(callback));
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
        if (data.toString().startsWith("sdktest://spid/login")) {
            String code = data.getQueryParameter("code");
            if (code.length() > 0) {
                getAccessToken(code);
                return true;
            } else {
                callback.onError(new Exception());
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
        public void onComplete(SPiDResponse result) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            try {
                if ((result.getJsonObject().has("error")) && ((String) result.getJsonObject().get("error")).length() > 0) {
                    callback.onError(new Exception());
                } else {
                    SPiDAccessToken token = new SPiDAccessToken(result.getJsonObject());
                    SPiDClient.getInstance().setAccessToken(token);
                    SPiDKeychain.encryptAccessTokenToSharedPreferences(SPiDClient.getInstance().getConfig().getContext(), SPiDClient.getInstance().getConfig().getClientSecret(), token);
                    SPiDClient.getInstance().runWaitingRequests();
                    callback.onComplete();
                }
            } catch (JSONException e) {
                callback.onError(new Exception());
            }
        }

        @Override
        public void onError(Exception exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            callback.onError(new Exception());
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
            callback.onComplete();
        }

        @Override
        public void onError(Exception exception) {
            SPiDClient.getInstance().clearAuthorizationRequest();
            callback.onError(new Exception());
        }
    }
}
