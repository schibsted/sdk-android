package com.schibsted.android.sdk;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 4:32 PM
 */
public class SPiDClient {
    private static final SPiDClient instance = new SPiDClient();

    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";
    //private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=authorization_code&response_type=code&platform=mobile&force=1";

    private SPiDConfiguration config;

    private SPiDAccessToken token;

    private SPiDAuthorizationRequest authorizationRequest;

    private SPiDClient() {
        config = null;
        token = null; // TODO: load from keychain?
        authorizationRequest = null;
    }

    public static SPiDClient getInstance() {
        return instance;
    }

    public void configure(SPiDConfiguration config) {
        this.config = config;
    }

    public String getAuthorizationURL() {
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    public WebView getAuthorizationWebView(Context context, SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        }
        // TODO: should we have this assert?
        assert authorizationRequest != null;
        SPiDLogger.log("Context: " + context.toString() + " url: " + getAuthorizationURL());
        return authorizationRequest.getAuthorizationWebView(context, getAuthorizationURL());
    }

    public void getCode(Uri data) {
        config.setCode(data.getQueryParameter("code"));
    }

    public void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void getCurrentUserRequest() {
        //SPiDRequest request = new SPiDRequest("GET", "https://stage.payment.schibsted.no/api/2/user/" + token.getUserID() + "?oauth_token=" + token.getAccessToken(), new AccessTokenCallback());

        //SPiDResponse response = sendRequest(request);
        //Log.i("SPiD", response.getJsonObject().toString());
    }

    public boolean handleIntent(Uri data) {
        if (authorizationRequest != null) {
            return authorizationRequest.handleIntent(data);
        }
        return false;
    }

    public void authorize(SPiDAsyncAuthorizationCallback authorizationCallback) {
        if (authorizationRequest == null) {
            authorizationRequest = new SPiDAuthorizationRequest(authorizationCallback);
        } else {
            // TODO: throw exception, only one authorization request can be running at a single time
        }
    }

    public SPiDConfiguration getConfig() {
        return config;
    }

    public void setAccessToken(SPiDAccessToken accessToken) {
        this.token = accessToken;
    }

    public void getCurrentUser() {
        //SPiDRequest request = new SPiDRequest("GET", "https://stage.payment.schibsted.no/api/2/user/" + token.getUserID() + "?oauth_token=" + token.getAccessToken(), new AccessTokenCallback());
    }

    public void refreshAccessToken(SPiDAsyncCallback sPiDAsyncCallback) {
    }

    public void logoutSPiDAPI(SPiDAsyncCallback sPiDAsyncCallback) {
        SPiDRequest request = new SPiDRequest("POST", "/api/{version}/me", sPiDAsyncCallback);
        //To change body of created methods use File | Settings | File Templates.
    }
}

