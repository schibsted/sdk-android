package com.spid.android.sdk.jwt;

/**
 * Created by oskarh on 01/04/15.
 */
public enum SubjectClaim {

    ATTACH("attach"),
    AUTHORIZATION("authorization"),
    REGISTRATION("registration");

    private final String claim;

    private SubjectClaim(final String claim) {
        this.claim = claim;
    }

    /*
     * @return The string representation of the subject claim
     */
    @Override
    public String toString() {
        return claim;
    }
}
