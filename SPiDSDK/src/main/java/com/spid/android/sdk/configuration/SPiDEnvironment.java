package com.spid.android.sdk.configuration;

/**
 * Declares which SPiD environment to run all api calls against
 */
public enum SPiDEnvironment {

    STAGE("https://identity-pre.schibsted.com"),
    PRODUCTION("https://login.schibsted.com"),
    NORWEGIAN_STAGE("https://identity-pre.schibsted.com"),
    NORWEGIAN_PRODUCTION("https://payment.schibsted.no"),
    SWEDISH_STAGE("https://identity-pre.schibsted.com"),
    SWEDISH_PRODUCTION("https://payment.schibsted.se");

    private final String url;

    private SPiDEnvironment(final String url) {
        this.url = url;
    }

    /*
     * @return The base URL of the environment
     */
    @Override
    public String toString() {
        return url;
    }
}
