package com.spid.android.sdk.utils;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.exceptions.SPiDException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Helper class for generating SPiD urls
 */
public final class SPiDUrl {

    private static final String AUTHORIZE_URL = "%s?client_id=%s&redirect_uri=%s&grant_type=%s&response_type=%s&platform=%s&force=%s";

    private enum Authorization {
        AUTHORIZATION,
        SIGNUP,
        FORGOT_PASSWORD
    }

    private SPiDUrl() {}

    /**
     * Generates URL for authorization in SPiD
     *
     * @return String for authorization
     * @throws UnsupportedEncodingException The unsupported encoding exception
     */
    public static String getAuthorizationURL() throws UnsupportedEncodingException {
        return getEncodedUrl(Authorization.AUTHORIZATION);
    }

    /**
     * Generates URL for signup in SPiD
     *
     * @return URL for signup
     * @throws UnsupportedEncodingException The unsupported encoding exception
     */
    public static String getSignupURL() throws UnsupportedEncodingException {
        return getEncodedUrl(Authorization.SIGNUP);
    }

    /**
     * Generates URL for lost password in SPiD
     *
     * @return URL for lost password
     * @throws UnsupportedEncodingException The unsupported encoding exception
     */
    public static String getForgotPasswordURL() throws UnsupportedEncodingException {
        return getEncodedUrl(Authorization.FORGOT_PASSWORD);
    }

    private static String getEncodedUrl(Authorization authorization) throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String url;
        switch(authorization) {
            case AUTHORIZATION:
                url = config.getAuthorizationURL();
                break;
            case SIGNUP:
                url = config.getSignupURL();
                break;
            case FORGOT_PASSWORD:
                url = config.getForgotPasswordURL();
                break;
            default:
                throw new SPiDException("Unsupported authorization type: " + authorization);
        }
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return String.format(AUTHORIZE_URL, url, config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for logout in SPiD
     *
     * @param accessToken Access token to logout
     * @return URL for logout
     * @throws UnsupportedEncodingException The unsupported encoding exception
     */
    public static String getLogoutURL(SPiDAccessToken accessToken) throws UnsupportedEncodingException {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String requestURL = config.getServerURL() + "/logout";
        String encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "spid/login", "UTF-8");
        return requestURL + "?redirect_uri=" + encodedRedirectURL + "&oauth_token=" + accessToken.getAccessToken();
    }
}
