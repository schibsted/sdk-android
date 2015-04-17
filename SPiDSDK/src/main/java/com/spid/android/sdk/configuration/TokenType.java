package com.spid.android.sdk.configuration;

/**
 * Created by oskarh on 31/03/15.
 */
public enum TokenType {

    FACEBOOK("facebook"),
    GOOGLE_PLUS("googleplus");

    private final String tokenTypeString;

    private TokenType(final String tokenTypeString) {
        this.tokenTypeString = tokenTypeString;
    }

    /*
     * @return The string representation of the token type, ie "facebook" or "googleplus"
     */
    @Override
    public String toString() {
        return tokenTypeString;
    }
}
