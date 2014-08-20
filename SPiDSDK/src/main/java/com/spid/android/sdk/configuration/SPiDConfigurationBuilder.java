package com.spid.android.sdk.configuration;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.logger.SPiDLogger;

/**
 * Builder class for SPiDConfiguration
 */
public class SPiDConfigurationBuilder {
    private String clientID;
    private String clientSecret;
    private String signSecret;
    private String appURLScheme;
    private String serverURL;
    private String redirectURL;
    private String authorizationURL;
    private String signupURL;
    private String forgotPasswordURL;
    private String tokenURL;
    private String serverClientID;
    private String serverRedirectUri;
    private Boolean useMobileWeb = Boolean.TRUE;
    private String apiVersion = "2";
    private Boolean debugMode = Boolean.FALSE;
    private Context context;

    /**
     * @param clientID SPiD client id
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder clientID(String clientID) {
        this.clientID = clientID;
        return this;
    }

    /**
     * @param clientSecret SPiD client secret
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * @param signSecret SPiD sign secret
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder signSecret(String signSecret) {
        this.signSecret = signSecret;
        return this;
    }

    /**
     * @param appURLScheme Android app url scheme
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder appURLScheme(String appURLScheme) {
        this.appURLScheme = appURLScheme;
        return this;
    }

    /**
     * @param serverURL SPiD server url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder serverURL(String serverURL) {
        this.serverURL = serverURL;
        return this;
    }

    /**
     * @param redirectURL SPiD redirect url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder redirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
        return this;
    }

    /**
     * @param authorizationURL SPiD authorization url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder authorizationURL(String authorizationURL) {
        this.authorizationURL = authorizationURL;
        return this;
    }

    /**
     * @param registrationURL SPiD registration url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder registrationURL(String registrationURL) {
        this.signupURL = registrationURL;
        return this;
    }

    /**
     * @param forgotPasswordURL SPiD lost password url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder forgotPasswordURL(String forgotPasswordURL) {
        this.forgotPasswordURL = forgotPasswordURL;
        return this;
    }

    /**
     * @param tokenURL SPiD token url
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder tokenURL(String tokenURL) {
        this.tokenURL = tokenURL;
        return this;
    }

    /**
     * @param serverClientID SPiD client id for server
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder serverClientID(String serverClientID) {
        this.serverClientID = serverClientID;
        return this;
    }

    /**
     * @param serverRedirectUri SPiD redirect uri for server
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder serverRedirectUri(String serverRedirectUri) {
        this.serverRedirectUri = serverRedirectUri;
        return this;
    }

    /**
     * @param apiVersion SPiD API version
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * @param debugMode Use debug mode
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder debugMode(Boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    /**
     * @param context Android application context
     * @return The SPiDConfigurationBuilder
     */
    public SPiDConfigurationBuilder context(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Checks that supplied string is not empty, otherwise throws exception
     *
     * @param string       The string to check
     * @param errorMessage Error message for the exception
     * @throws IllegalArgumentException
     */
    protected void isEmptyString(String string, String errorMessage) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks if supplied object is not null, otherwise throws exception
     *
     * @param object       The object to check
     * @param errorMessage Error message for the exception
     * @throws IllegalArgumentException
     */
    protected void isNull(Object object, String errorMessage) {
        if (object == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Setup custom User-Agent for all SPiD requests
     *
     * @return Custom User-Agent
     */

    private String getUserAgent() {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        try {
            if (packageManager != null) {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            }
        } catch (final PackageManager.NameNotFoundException e) {
            SPiDLogger.log("Could not get package info");
        }

        String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "UnknownApplication");
        String applicationVersion = packageInfo != null ? packageInfo.versionName : "UnknownVersion";

        return applicationName + "/" + applicationVersion + " " + "SPiDAndroidSDK/" + SPiDClient.SPID_ANDROID_SDK_VERSION_STRING + " " + "Android/" + android.os.Build.MODEL + "/API " + Build.VERSION.SDK_INT;
    }

    /**
     * Builds a SPiDConfiguration object from the supplied values. It also check all that all mandatory values are set and generates default values for non-mandatory values that are missing.
     *
     * @return A SPiDConfiguration object
     */
    public SPiDConfiguration build() {
        isEmptyString(clientID, "ClientID is missing");
        isEmptyString(clientSecret, "ClientSecret is missing");
        isEmptyString(appURLScheme, "AppURLScheme is missing");
        isEmptyString(serverURL, "ServerURL is missing");
        isNull(context, "Context is missing");

        if (redirectURL == null || TextUtils.isEmpty(redirectURL.trim())) {
            redirectURL = appURLScheme + "://";
        }

        if (authorizationURL == null || TextUtils.isEmpty(authorizationURL.trim())) {
            authorizationURL = serverURL + "/auth/login";
        }

        if (tokenURL == null || TextUtils.isEmpty(tokenURL.trim())) {
            tokenURL = serverURL + "/oauth/token";
        }

        if (signupURL == null || TextUtils.isEmpty(signupURL.trim())) {
            signupURL = serverURL + "/auth/signup";
        }

        if (forgotPasswordURL == null || TextUtils.isEmpty(forgotPasswordURL.trim())) {
            forgotPasswordURL = serverURL + "/auth/forgotpassword";
        }

        if (serverClientID == null || TextUtils.isEmpty(serverClientID.trim())) {
            serverClientID = clientID;
        }

        if (serverRedirectUri == null || TextUtils.isEmpty(serverRedirectUri.trim())) {
            serverRedirectUri = redirectURL;
        }

        String userAgent = getUserAgent();

        return new SPiDConfiguration(
                clientID,
                clientSecret,
                signSecret,
                appURLScheme,
                serverURL,
                redirectURL,
                authorizationURL,
                signupURL,
                forgotPasswordURL,
                tokenURL,
                serverClientID,
                serverRedirectUri,
                useMobileWeb,
                apiVersion,
                debugMode,
                userAgent,
                context);
    }
}
