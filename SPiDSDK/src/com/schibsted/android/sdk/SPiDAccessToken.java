package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/9/12
 * Time: 4:13 PM
 */
public class SPiDAccessToken {

    private String accessToken;
    private Date expiresAt;
    private String refreshToken;
    private String userID;

    public SPiDAccessToken(JSONObject jsonObject) {
        SPiDLogger.log(String.format("Got JSON: %s", jsonObject.toString()));
        try {
            this.accessToken = jsonObject.getString("access_token");
            this.refreshToken = jsonObject.getString("refresh_token");

            Integer expiresIn = jsonObject.getInt("expires_in");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, expiresIn);

            expiresAt = cal.getTime();

            this.userID = jsonObject.getString("user_id");
        } catch (JSONException e) {
            throw new SPiDInvalidResponseException("Received invalid token response", e);
        }
    }

    public SPiDAccessToken(String accessToken, Long expiresAt, String refreshToken, String userID) {
        SPiDLogger.log(String.format("Loaded from SharedPref: %s, %s, %s, %s", accessToken, expiresAt, refreshToken, userID));
        this.accessToken = accessToken;
        this.expiresAt = new Date(expiresAt);
        this.refreshToken = refreshToken;
        this.userID = userID;
        SPiDLogger.log(String.format("Loaded from SharedPref: %s, %s, %s, %s", accessToken, this.expiresAt.toString(), refreshToken, userID));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
