package com.spid.android.sdk.jwt;

public enum Audience {

    SIGN_UP("/api/2/signup_jwt"),
    ATTACH("/api/2/user/attach_jwt");

    private final String audience;

    Audience(final String audience) {
        this.audience = audience;
    }

    /*
     * @return The string representation of the audience
     */
    @Override
    public String toString() {
        return audience;
    }
}
