package com.spid.android.sdk.jwt;

public enum SubjectClaim {

    ATTACH("attach"),
    AUTHORIZATION("authorization"),
    REGISTRATION("registration");

    private final String claim;

    SubjectClaim(final String claim) {
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
