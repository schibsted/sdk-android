package com.spid.android.sdk.configuration;

public enum TokenType {

    FACEBOOK("facebook"),
    GOOGLE_PLUS("googleplus");

    private final String tokenTypeString;

    TokenType(final String tokenTypeString) {
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
