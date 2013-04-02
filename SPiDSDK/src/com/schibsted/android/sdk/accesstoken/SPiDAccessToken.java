package com.schibsted.android.sdk.accesstoken;

import com.schibsted.android.sdk.exceptions.SPiDAccessTokenException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains a access token response from SPiD
 */
public class SPiDAccessToken {

    public static String SPiDAccessTokenKey = "access_token";
    public static String SPiDAccessTokenExpiresInKey = "expires_in";
    public static String SPiDAccessTokenRefreshTokenKey = "refresh_token";
    public static String SPiDAccessTokenUserIdKey = "user_id";

    private String accessToken;
    private Date expiresAt;
    private String refreshToken;
    private String userID;

    /**
     * Constructor for SPiDAccessToken object, used with response from SPiD
     *
     * @param jsonObject Parsed JSON response from SPiD
     */
    public SPiDAccessToken(JSONObject jsonObject) {
        try {
            this.accessToken = jsonObject.getString(SPiDAccessTokenKey);

            Integer expiresIn = jsonObject.getInt(SPiDAccessTokenExpiresInKey);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, expiresIn);

            this.expiresAt = cal.getTime();

            // Optional values
            this.refreshToken = jsonObject.optString(SPiDAccessTokenRefreshTokenKey, null);
            this.userID = jsonObject.optString(SPiDAccessTokenUserIdKey, null);
        } catch (JSONException e) {
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

    public boolean isClientToken() {
        return (userID != null  && !userID.equals("0"));
    }
}