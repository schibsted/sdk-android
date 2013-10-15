package com.schibsted.android.sdk.webview;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;
import com.schibsted.android.sdk.exceptions.SPiDUserAbortedLoginException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.request.SPiDCodeTokenRequest;
import com.schibsted.android.sdk.request.SPiDTokenRequest;

/**
 * SPiD implementation of WebViewClient, it should be subclassed if a custom WebViewClient is needed
 */
public class SPiDWebViewClient extends WebViewClient {

    private SPiDAuthorizationListener listener;

    /**
     * @param listener Called on completion or error, can be <code>null</code>
     */
    public void setListener(SPiDAuthorizationListener listener) {
        this.listener = listener;
    }

    /**
     * Called when the WebView encounters a unrecoverable error.
     *
     * @param view        The WebView that is initiating the callback.
     * @param errorCode   The error code corresponding to an ERROR_* value.
     * @param description A String describing the error.
     * @param failingUrl  The url that failed to load.
     */
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (listener != null)
            listener.onSPiDException(new SPiDInvalidResponseException("Received invalid response with code: " + errorCode + " and description" + description));
    }

    /**
     * Checks if the url should be loaded in the WebView or if its a application callback.
     *
     * @param view The WebView that is initiating the callback.
     * @param url  The url to be loaded.
     * @return True if the host application wants to leave the current WebView and handle the url itself, otherwise return false
     */
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (url.startsWith(SPiDClient.getInstance().getConfig().getAppURLScheme())) {
            if (uri.getPath().endsWith("login")) {
                String code = uri.getQueryParameter("code");
                if (code == null) {
                    if (listener != null) {
                        listener.onSPiDException(new SPiDUserAbortedLoginException("User aborted login"));
                    } else {
                        SPiDLogger.log("User aborted login");
                    }
                    SPiDClient.getInstance().clearAuthorizationRequest();
                } else if (code.length() > 0) {
                    SPiDTokenRequest request = new SPiDCodeTokenRequest(code, SPiDClient.getInstance().getAuthorizationListener());
                    request.execute();
                } else {
                    if (listener != null) {
                        listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                    } else {
                        SPiDLogger.log("Received invalid code");
                    }
                    SPiDClient.getInstance().clearAuthorizationRequest();
                }
                return true;
            } else if (uri.getPath().endsWith("failure")) {
                if (listener != null) {
                    listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                } else {
                    SPiDLogger.log("Received invalid code");
                }
                SPiDClient.getInstance().clearAuthorizationRequest();
            }

        } else {
            view.loadUrl(url);
        }
        return false;
    }
}
