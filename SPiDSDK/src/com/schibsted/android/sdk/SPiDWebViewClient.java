package com.schibsted.android.sdk;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 11/5/12
 * Time: 12:48 PM
 */
public class SPiDWebViewClient extends WebViewClient {

    private SPiDAuthorizationListener listener;

    public void setListener(SPiDAuthorizationListener listener) {
        this.listener = listener;
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (listener != null)
            listener.onSPiDException(new SPiDInvalidResponseException("Received invalid response with code: " + errorCode + " and description" + description));
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (url.startsWith(SPiDClient.getInstance().getConfig().getAppURLScheme())) {
            if (uri.getPath().endsWith("login")) {
                String code = uri.getQueryParameter("code");
                if (code.length() > 0) {
                    SPiDClient.getInstance().requestAccessToken(code);
                } else {
                    if (listener != null)
                        listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                    else
                        SPiDLogger.log("Received invalid code");
                }
                return true;
            } else if (uri.getPath().endsWith("failure")) {
                if (listener != null)
                    listener.onSPiDException(new SPiDInvalidResponseException("Received invalid code"));
                else
                    SPiDLogger.log("Received invalid code");
            }

        } else {
            view.loadUrl(url);
        }
        return false;
    }
}
