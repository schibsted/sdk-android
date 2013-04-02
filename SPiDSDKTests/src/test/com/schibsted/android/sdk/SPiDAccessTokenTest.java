package com.schibsted.android.sdk;

import com.schibsted.android.sdk.accesstoken.SPiDAccessToken;
import com.schibsted.android.sdk.exceptions.SPiDAccessTokenException;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class SPiDAccessTokenTest {

    private static String TestAccessToken = "test-access-token";
    private static Long TestExpiresIn = Long.valueOf(60);
    private static String TestRefreshTokenString = "test-refresh-token";
    private static String TestUserIdString = "test-user-id";

    private JSONObject jsonObject;

    @Before
    public void setUp() throws Exception {
        jsonObject = new JSONObject()
                .put(SPiDAccessToken.SPiDAccessTokenKey, TestAccessToken)
                .put(SPiDAccessToken.SPiDAccessTokenExpiresInKey, TestExpiresIn)
                .put(SPiDAccessToken.SPiDAccessTokenRefreshTokenKey, TestRefreshTokenString)
                .put(SPiDAccessToken.SPiDAccessTokenUserIdKey, TestUserIdString);
    }

    @org.junit.Test
    public void testJSONObjectConstructor() throws Exception {
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, jsonObject.getInt(SPiDAccessToken.SPiDAccessTokenExpiresInKey));
        Date expiresAt = cal.getTime();

        assertNotNull(accessToken);
        assertEquals(TestAccessToken, accessToken.getAccessToken());
        assertEquals(TestRefreshTokenString, accessToken.getRefreshToken());
        assertEquals(TestUserIdString, accessToken.getUserID());
        assertTrue(Math.abs(expiresAt.getTime() - accessToken.getExpiresAt().getTime()) < 1000);
    }

    @org.junit.Test(expected = SPiDAccessTokenException.class)
    public void testJSONObjectConstructorWithMissingAccessToken() throws Exception {
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenKey);
        new SPiDAccessToken(jsonObject);
    }

    @org.junit.Test(expected = SPiDAccessTokenException.class)
    public void testJSONObjectConstructorWithMissingExpiresIn() throws Exception {
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenExpiresInKey);
        new SPiDAccessToken(jsonObject);
    }

    @org.junit.Test
    public void testDataConstructor() throws Exception {
        Long expiresAt = new Date().getTime();
        SPiDAccessToken accessToken = new SPiDAccessToken(TestAccessToken, expiresAt, TestUserIdString, TestRefreshTokenString);
        assertNotNull(accessToken);
    }

    @org.junit.Test(expected = SPiDAccessTokenException.class)
    public void testDataConstructorWithMissingAccessToken() throws Exception {
        Long expiresAt = new Date().getTime();
        new SPiDAccessToken(null, expiresAt, TestUserIdString, TestRefreshTokenString);
    }

    @org.junit.Test(expected = SPiDAccessTokenException.class)
    public void testDataConstructorWithMissingExpiresIn() throws Exception {
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenExpiresInKey);
        new SPiDAccessToken(TestAccessToken, null, TestUserIdString, TestRefreshTokenString);
    }

    @org.junit.Test
    public void testGetAccessToken() throws Exception {
        String accessTokenString = "test-access-token-string";
        jsonObject.put(SPiDAccessToken.SPiDAccessTokenKey, accessTokenString);
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);
        assertEquals(accessTokenString, accessToken.getAccessToken());
    }

    @org.junit.Test
    public void testGetExpiresAt() throws Exception {
        Date date = new Date();
        SPiDAccessToken accessToken = new SPiDAccessToken(TestAccessToken, date.getTime(), TestUserIdString, TestRefreshTokenString);
        assertEquals(date.getTime(), accessToken.getExpiresAt().getTime());
    }

    @org.junit.Test
    public void testGetRefreshToken() throws Exception {
        String refreshTokenString = "test-refresh-token-string";
        jsonObject.put(SPiDAccessToken.SPiDAccessTokenRefreshTokenKey, refreshTokenString);
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenUserIdKey);
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);
        assertEquals(refreshTokenString, accessToken.getRefreshToken());
    }

    @org.junit.Test
    public void testGetRefreshTokenWithEmptyRefreshToken() throws Exception {
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenRefreshTokenKey);
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenUserIdKey);
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);
        assertNull(accessToken.getRefreshToken());
    }

    @org.junit.Test
    public void testGetUserID() throws Exception {
        String userIdString = "test-user-id-string";
        jsonObject.put(SPiDAccessToken.SPiDAccessTokenUserIdKey, userIdString);
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenRefreshTokenKey);
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);
        assertEquals(userIdString, accessToken.getUserID());
    }

    @org.junit.Test
    public void testGetUserIDWithEmptyUserID() throws Exception {
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenRefreshTokenKey);
        jsonObject.remove(SPiDAccessToken.SPiDAccessTokenUserIdKey);
        SPiDAccessToken accessToken = new SPiDAccessToken(jsonObject);
        assertNull(accessToken.getUserID());
    }
}
