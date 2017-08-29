package com.spid.android.sdk.utils;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.logger.SPiDLogger;

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
     */
    public static String getAuthorizationURL() {
        return getEncodedUrl(Authorization.AUTHORIZATION);
    }

    /**
     * Generates URL for signup in SPiD
     *
     * @return URL for signup
     */
    public static String getSignupURL() {
        return getEncodedUrl(Authorization.SIGNUP);
    }

    /**
     * Generates URL for lost password in SPiD
     *
     * @return URL for lost password
     */
    public static String getForgotPasswordURL() {
        return getEncodedUrl(Authorization.FORGOT_PASSWORD);
    }

    private static String getEncodedUrl(Authorization authorization) {
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
        String encodedRedirectURL = getEncodedLoginUrl();
        return String.format(AUTHORIZE_URL, url, config.getClientID(), encodedRedirectURL, "authorization_code", "code", "mobile", "1");
    }

    /**
     * Generates URL for logout in SPiD
     *
     * @param accessToken Access token to logout
     * @return URL for logout
     */
    public static String getLogoutURL(SPiDAccessToken accessToken) {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String requestURL = config.getServerURL() + "/logout";
        String encodedRedirectURL = getEncodedLoginUrl();
        return requestURL + "?redirect_uri=" + encodedRedirectURL + "&oauth_token=" + accessToken.getAccessToken();
    }

    private static String getEncodedLoginUrl() {
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        String encodedRedirectURL;
        final String encoding = "UTF-8";
        try {
            encodedRedirectURL = URLEncoder.encode(config.getRedirectURL() + "login", encoding);
        } catch(UnsupportedEncodingException uee) {
            // Shouldn't be possible since we use UTF-8 which is default in Android
            SPiDLogger.log("Failed to getEncodedLoginUrl url " + config.getRedirectURL() + " using encoding " + encoding);
            encodedRedirectURL = config.getRedirectURL() + "login";
        }
        return encodedRedirectURL;
    }
}
