package com.schibsted.android.sdk;

import android.net.Uri;
import android.util.Log;

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

    private SPiDClient() {
    }

    public static SPiDClient getInstance() {
        return instance;
    }

    public void configure(SPiDConfiguration config) {
        this.config = config;
    }

    public String getAuthorizationURL() {
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "login");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    public void getCode(Uri data) {
        config.setCode(data.getQueryParameter("code"));
    }

    public void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().equals("")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void getAccessToken() {
        isEmptyString(config.getCode(), "No code available");

        SPiDRequest request = new SPiDRequest("POST", "https://stage.payment.schibsted.no/oauth/token");
        request.addBodyParameter("grant_type", "authorization_code");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("code", config.getCode());
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "login");

        SPiDResponse response = sendRequest(request);
        token = new SPiDAccessToken(response.getJsonObject());
    }

    public SPiDResponse sendRequest(SPiDRequest request) {
        SPiDResponse response = null;
        try {
            response = request.send();
        } catch (Exception e) {
            Log.i("asdf", "asdf");
        }
        return response;
    }

    public void getCurrentUserRequest() {
        SPiDRequest request = new SPiDRequest("GET", "https://stage.payment.schibsted.no/api/2/user/" + token.getUserID() + "?oauth_token=" + token.getAccessToken());

        SPiDResponse response = sendRequest(request);
        Log.i("SPiD", response.getJsonObject().toString());
    }
}

