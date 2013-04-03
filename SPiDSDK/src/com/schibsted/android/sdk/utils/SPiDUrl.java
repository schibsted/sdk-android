package com.schibsted.android.sdk.utils;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.accesstoken.SPiDAccessToken;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Helper class for generating SPiD urls
 */
public class SPiDUrl {
    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";

    /**
     * Generates URL for authorization in SPiD
     *
     * @return URL for authorization
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static String getAuthorizationURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getAuthorizationURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for signup in SPiD
     *
     * @return URL for signup
     * @throws UnsupportedEncodingException
     */
    public static String getSignupURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getSignupURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for lost password in SPiD
     *
     * @return URL for lost password
     * @throws UnsupportedEncodingException
     */
    public static String getForgotPasswordURL() throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, config.getForgotPasswordURL(), config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for logout in SPiD
     *
     * @param accessToken Access token to logout
     * @return URL for logout
     * @throws UnsupportedEncodingException
     */
    public static String getLogoutURL(SPiDAccessToken accessToken) throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String requestURL = SPiDClient.getInstance().getConfig().getServerURL() + "/logout";
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return requestURL + "?redirect_uri=" + encodedRedirectURL + "&oauth_token=" + accessToken.getAccessToken();
    }
}
