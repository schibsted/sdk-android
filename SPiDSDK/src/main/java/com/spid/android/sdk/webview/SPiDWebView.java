package com.spid.android.sdk.webview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.exceptions.SPiDAuthorizationAlreadyRunningException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.utils.SPiDUrl;

import java.io.UnsupportedEncodingException;

/**
 * Helper methods to create various webviews
 */
public final class SPiDWebView {

    private SPiDWebView() {}

    /**
     * Sets up a WebView with SPiD login
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD authorization, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @param listener      Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static WebView webViewAuthorization(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        SPiDAuthorizationListener authorizationListener = SPiDClient.getInstance().getAuthorizationListener();
        if (authorizationListener == null) {
            SPiDClient.getInstance().setAuthorizationListener(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return getAuthorizationWebView(context, webView, webViewClient);
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
    public static WebView webViewAuthorization(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return webViewAuthorization(context, webView, null, listener);
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
    private static WebView getAuthorizationWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url = SPiDUrl.getAuthorizationURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD signup
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD signup, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @param listener      Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public static WebView webViewSignup(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        SPiDAuthorizationListener authorizationListener = SPiDClient.getInstance().getAuthorizationListener();
        if (authorizationListener == null) {
            SPiDClient.getInstance().setAuthorizationListener(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }

        return getSignupWebView(context, webView, webViewClient);
    }

    /**
     * Sets up a WebView with SPiD signup
     *
     * @param context  Android application context
     * @param webView  WebView that should be instantiated to SPiD signup, creates a new WebView if <code>null</code>
     * @param listener Listener called on completion or failure, can be <code>null</code>
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    public static WebView webViewSignup(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return webViewSignup(context, webView, null, listener);
    }


    /**
     * Sets up a WebView with SPiD signup
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD signup, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    private static WebView getSignupWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url = SPiDUrl.getSignupURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
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
    public static WebView webViewForgotPassword(Context context, WebView webView, SPiDWebViewClient webViewClient, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        SPiDAuthorizationListener authorizationListener = SPiDClient.getInstance().getAuthorizationListener();
        if (authorizationListener == null) {
            SPiDClient.getInstance().setAuthorizationListener(listener);
        } else {
            throw new SPiDAuthorizationAlreadyRunningException("Authorization already running");
        }
        return getForgotPasswordWebView(context, webView, webViewClient);
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
    public static WebView webViewForgotPassword(Context context, WebView webView, SPiDAuthorizationListener listener) throws UnsupportedEncodingException {
        return webViewForgotPassword(context, webView, null, listener);
    }

    /**
     * Sets up a WebView with SPiD lost password
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated to SPiD lost password, creates a new WebView if <code>null</code>
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     * @throws UnsupportedEncodingException
     */
    private static WebView getForgotPasswordWebView(Context context, WebView webView, SPiDWebViewClient webViewClient) throws UnsupportedEncodingException {
        String url = SPiDUrl.getForgotPasswordURL().concat("&webview=1");
        return getWebView(context, webView, url, webViewClient);
    }

    /**
     * Sets up a WebView with the provided URL
     *
     * @param context       Android application context
     * @param webView       WebView that should be instantiated, creates a new WebView if <code>null</code>
     * @param url           URL to open
     * @param webViewClient SPiDWebViewClient to be used with WebView, mainly for onPageStarted and onPageFinished
     * @return The WebView
     */
    public static WebView getWebView(final Context context, WebView webView, String url, SPiDWebViewClient webViewClient) {
        if (SPiDClient.getInstance().isAuthorized()) {
            SPiDLogger.log("Access token found, performing a soft logout to cleanup before login");
            // Fire and forget
            SPiDClient.getInstance().apiLogout(null);
            SPiDClient.getInstance().clearAccessToken();
        }
        if (webView == null) {
            webView = new WebView(context);
        }

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Fix bug where input never get virtual keyboard focus see: http://code.google.com/p/android/issues/detail?id=7189
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setUseWideViewPort(false);
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
        webViewClient.setListener(SPiDClient.getInstance().getAuthorizationListener());

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);
        return webView;
    }
}
