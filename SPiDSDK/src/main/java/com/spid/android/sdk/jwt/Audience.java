package com.spid.android.sdk.jwt;

/**
 * Created by oskarh on 14/04/15.
 */
public enum Audience {

    SIGN_UP("http://spp.dev/api/2/signup_jwt"),
    ATTACH("http://spp.dev/api/2/user/attach_jwt");

    private final String audience;

    private Audience(final String audience) {
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
