package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 9:20 PM
 */

/*
conn.setRequestProperty("User-Agent", System.getProperties().
                getProperty("http.agent") + " FacebookAndroidSDK");
 */
public class SPiDAuthorizationRequest {
    private SPiDAsyncAuthorizationCallback callback;

    public SPiDAuthorizationRequest(SPiDAsyncAuthorizationCallback authorizationCallback) {
        this.callback = authorizationCallback;
    }

    public WebView getAuthorizationWebView(final Context context, String url) {
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

    class AccessTokenCallback implements SPiDAsyncCallback {
        private SPiDAsyncAuthorizationCallback callback;

        public AccessTokenCallback(SPiDAsyncAuthorizationCallback callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void onComplete(SPiDResponse result) {
            try {
                if ((result.getJsonObject().has("error")) && ((String) result.getJsonObject().get("error")).length() > 0) {
                    callback.onError(new Exception());
                } else {
                    SPiDClient.getInstance().setAccessToken(new SPiDAccessToken(result.getJsonObject()));
                    callback.onComplete();
                }
            } catch (JSONException e) {
                callback.onError(new Exception());
            }
        }

        @Override
        public void onError(Exception exception) {
            callback.onError(new Exception());
        }
    }
}
