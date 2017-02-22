package com.spid.android.sdk;

import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.exceptions.SPiDAccessTokenException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SPiDAccessTokenTest {

    private final String testAccessToken = "test-access-token";
    private final Long testExpiresIn = Long.valueOf(60);
    private final String testRefreshTokenString = "test-refresh-token";
    private final String testUserIdString = "test-user-id";

    private JSONObject jsonAccessToken;

    @Before
    public void setUp() throws Exception {
        jsonAccessToken = mock(JSONObject.class);
        when(jsonAccessToken.getString(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY)).thenReturn(testAccessToken);
        when(jsonAccessToken.getInt(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY_EXPIRES_IN)).thenReturn(testExpiresIn.intValue());
        when(jsonAccessToken.optString(SPiDAccessToken.REFRESH_SPID_ACCESS_TOKEN_KEY, null)).thenReturn(testRefreshTokenString);
        when(jsonAccessToken.optString(SPiDAccessToken.SPID_ACCESS_TOKEN_USER_ID, null)).thenReturn(testUserIdString);
    }

    @Test
    public void SPiDAccessToken_fromCorrectJSON_createsAccessToken() throws Exception {
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonAccessToken);

        verifyValidToken(accessToken);
        assertEquals(testUserIdString, accessToken.getUserID());
        assertEquals(testRefreshTokenString, accessToken.getRefreshToken());
    }

    @Test
    public void SPiDAccessToken_fromCorrectParams_createsAccessToken() throws Exception {
        SPiDAccessToken accessToken = new SPiDAccessToken(testAccessToken, testExpiresIn, testRefreshTokenString, testUserIdString);

        verifyValidToken(accessToken);
        assertEquals(testUserIdString, accessToken.getUserID());
        assertEquals(testRefreshTokenString, accessToken.getRefreshToken());
    }

    @Test
    public void SPiDAccessToken_missingOptionalUserIdAndRefreshToken_createsAccessToken() throws Exception {
        SPiDAccessToken accessToken = new SPiDAccessToken(testAccessToken, testExpiresIn, null, null);

        verifyValidToken(accessToken);
    }

    private void verifyValidToken(SPiDAccessToken accessToken) {
        assertEquals(testAccessToken, accessToken.getAccessToken());
        assertNotNull(accessToken.getExpiresAt());
    }

    @Test(expected = SPiDAccessTokenException.class)
    public void SPiDAccessToken_missingAccessToken_throwsSPiDAccessTokenException() throws Exception {
        when(jsonAccessToken.getString(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY)).
                thenThrow(new JSONException(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY + " not present"));

        new SPiDAccessToken(jsonAccessToken);
    }

    @Test(expected = SPiDAccessTokenException.class)
    public void SPiDAccessToken_missingExpiresIn_throwsSPiDAccessTokenException() throws Exception {
        when(jsonAccessToken.getInt(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY_EXPIRES_IN)).
                thenThrow(new JSONException(SPiDAccessToken.SPID_ACCESS_TOKEN_KEY_EXPIRES_IN + " not present"));

        new SPiDAccessToken(jsonAccessToken);
    }


    @Test(expected = SPiDAccessTokenException.class)
    public void SPiDAccessToken_missingAccessTokenParam_throwsSPiDAccessTokenException() throws Exception {
        new SPiDAccessToken(null, testExpiresIn, testUserIdString, testRefreshTokenString);
    }

    @Test(expected = SPiDAccessTokenException.class)
    public void SPiDAccessToken_missingExpiresParam_throwsSPiDAccessTokenException() throws Exception {
        new SPiDAccessToken(testAccessToken, null, testUserIdString, testRefreshTokenString);
    }

    @Test
    public void isClientToken_userIdNull_returnsTrue() throws Exception {
        when(jsonAccessToken.optString(SPiDAccessToken.SPID_ACCESS_TOKEN_USER_ID, null)).thenReturn(null);

        SPiDAccessToken accessToken = new SPiDAccessToken(jsonAccessToken);

        assertTrue(accessToken.isClientToken());
    }

    @Test
    public void isClientToken_userIdZero_returnsTrue() throws Exception {
        when(jsonAccessToken.optString(SPiDAccessToken.SPID_ACCESS_TOKEN_USER_ID, null)).thenReturn("0");

        SPiDAccessToken accessToken = new SPiDAccessToken(jsonAccessToken);

        assertTrue(accessToken.isClientToken());
    }

    @Test
    public void isClientToken_userIdSet_returnsFalse() throws Exception {
        when(jsonAccessToken.optString(SPiDAccessToken.SPID_ACCESS_TOKEN_USER_ID, null)).thenReturn(testUserIdString);

        SPiDAccessToken accessToken = new SPiDAccessToken(jsonAccessToken);

        assertFalse(accessToken.isClientToken());
    }
}
