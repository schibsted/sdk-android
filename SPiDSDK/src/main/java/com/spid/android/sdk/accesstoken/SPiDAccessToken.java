package com.spid.android.sdk.accesstoken;

import com.spid.android.sdk.exceptions.SPiDAccessTokenException;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains a access token response from SPiD
 */
public class SPiDAccessToken {

    public static final String USER_ID = "userId";
    public static final String SPID_ACCESS_TOKEN_EVENT = "com.spid.android.sdk.ACCESS_TOKEN_EVENT";
    public static final String SPID_ACCESS_TOKEN_KEY = "access_token";
    public static final String SPID_ACCESS_TOKEN_KEY_EXPIRES_IN = "expires_in";
    public static final String REFRESH_SPID_ACCESS_TOKEN_KEY = "refresh_token";
    public static final String SPID_ACCESS_TOKEN_USER_ID = "user_id";

    private final String accessToken;
    private final Date expiresAt;
    private final String refreshToken;
    private final String userID;

    /**
     * Constructor for SPiDAccessToken object, used with response from SPiD
     *
     * @param jsonObject Parsed JSON response from SPiD
     */
    public SPiDAccessToken(JSONObject jsonObject) {
        try {
            this.accessToken = jsonObject.getString(SPID_ACCESS_TOKEN_KEY);

            Integer expiresIn = jsonObject.getInt(SPID_ACCESS_TOKEN_KEY_EXPIRES_IN);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, expiresIn);

            this.expiresAt = cal.getTime();

            // Optional values
            this.refreshToken = jsonObject.optString(REFRESH_SPID_ACCESS_TOKEN_KEY, null);
            this.userID = jsonObject.optString(SPID_ACCESS_TOKEN_USER_ID, null);
        } catch (Exception e) { // Could be NullPointerException, JsonException (and more?)
            throw new SPiDAccessTokenException("Received invalid access token data");
        }
    }

    /**
     * Constructor for SPiDAccessToken object, used when loading from SharedPreferences
     *
     * @param accessToken  Access token
     * @param expiresAt    Date when access token expires
     * @param refreshToken Refresh token
     * @param userID       User id for the access token
     */
    public SPiDAccessToken(String accessToken, Long expiresAt, String refreshToken, String userID) {
        if (accessToken == null || expiresAt == null) {
            throw new SPiDAccessTokenException("Received invalid access token data");
        }

        this.accessToken = accessToken;
        this.expiresAt = new Date(expiresAt);
        this.refreshToken = refreshToken;
        this.userID = userID;
    }

    /**
     * @return <code>true</code> if the access token that has not expired
     */
    public boolean isAuthorized() {
        Date currentMoment = new Date();
        return currentMoment.before(getExpiresAt());
    }

    /**
     * @return Access token used to make requests to SPiD
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return Expiry date for the access token
     */
    public Date getExpiresAt() {
        return expiresAt;
    }

    /**
     * @return The refresh token that should be used to refresh the access token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @return The user id that associated with this access token
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @return <code>true</code> if token is a client token, otherwise false
     */
    public boolean isClientToken() {
        return (userID == null || Boolean.FALSE.toString().equalsIgnoreCase(userID) || userID.equals("0"));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        SPiDAccessToken comparedToken = (SPiDAccessToken) other;

        if (accessToken != null ? !accessToken.equals(comparedToken.accessToken) : comparedToken.accessToken != null)
            return false;
        if (expiresAt != null ? !expiresAt.equals(comparedToken.expiresAt) : comparedToken.expiresAt != null)
            return false;
        if (refreshToken != null ? !refreshToken.equals(comparedToken.refreshToken) : comparedToken.refreshToken != null)
            return false;
        return !(userID != null ? !userID.equals(comparedToken.userID) : comparedToken.userID != null);

    }

    @Override
    public int hashCode() {
        int result = accessToken != null ? accessToken.hashCode() : 0;
        result = 31 * result + (expiresAt != null ? expiresAt.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }
}