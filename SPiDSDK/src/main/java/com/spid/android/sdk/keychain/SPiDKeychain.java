package com.spid.android.sdk.keychain;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;

import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.exceptions.SPiDKeychainException;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.utils.SPiDUtils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Helper class used to securely encrypt/decrypt access token to SharedPreferences
 */
public class SPiDKeychain {
    protected static final String UTF8 = "utf-8";

    /**
     * Encrypts access token and saves it to SharedPreferences
     *
     * @param encryptionKey Key used to encrypt the access token
     * @param accessToken   Access token to be saved
     */
    public static void encryptAccessTokenToSharedPreferences(String encryptionKey, SPiDAccessToken accessToken) {
        SPiDLogger.log("Saving: " + accessToken.getAccessToken() + ", " + Long.toString(accessToken.getExpiresAt().getTime()) + ", " + accessToken.getRefreshToken() + ", " + accessToken.getUserID());
        SharedPreferences secure = SPiDUtils.getSecurePreferencesFile();
        SharedPreferences.Editor editor = secure.edit();
        try {
            editor.putString("access_token", encryptString(encryptionKey, accessToken.getAccessToken()));
            editor.putString("expires_at", encryptString(encryptionKey, Long.toString(accessToken.getExpiresAt().getTime())));
            editor.putString("refresh_token", encryptString(encryptionKey, accessToken.getRefreshToken()));
            editor.putString("user_id", encryptString(encryptionKey, accessToken.getUserID()));
        } catch (GeneralSecurityException e) {
            clearAccessTokenFromSharedPreferences();
            throw new SPiDKeychainException("GeneralSecurityException", e);
        } catch (UnsupportedEncodingException e) {
            clearAccessTokenFromSharedPreferences();
            throw new SPiDKeychainException("UnsupportedEncodingException", e);
        }
        editor.apply();
    }

    /**
     * Decrypts access token from SharedPreferences
     *
     * @param encryptionKey Key used to decrypt the access token
     * @return Access token if found, otherwise null
     */
    public static SPiDAccessToken decryptAccessTokenFromSharedPreferences(String encryptionKey) {
        SharedPreferences secure = SPiDUtils.getSecurePreferencesFile();
        if (secure.contains("access_token")) {
            SPiDAccessToken token;
            try {
                String accessToken = decryptString(encryptionKey, secure.getString("access_token", ""));
                Long expiresAt = Long.valueOf(decryptString(encryptionKey, secure.getString("expires_at", "")));
                String refreshToken = decryptString(encryptionKey, secure.getString("refresh_token", ""));
                String userId = decryptString(encryptionKey, secure.getString("user_id", ""));
                token = new SPiDAccessToken(accessToken, expiresAt, refreshToken, userId);
            } catch (GeneralSecurityException e) {
                clearAccessTokenFromSharedPreferences();
                throw new SPiDKeychainException("GeneralSecurityException", e);
            } catch (UnsupportedEncodingException e) {
                clearAccessTokenFromSharedPreferences();
                throw new SPiDKeychainException("UnsupportedEncodingException", e);
            }
            return token;
        } else {
            return null;
        }
    }

    /**
     * Clears access token from SharedPreferences
     */
    public static void clearAccessTokenFromSharedPreferences() {
        SharedPreferences secure = SPiDUtils.getSecurePreferencesFile();
        SharedPreferences.Editor editor = secure.edit();
        editor.remove("access_token");
        editor.remove("expires_at");
        editor.remove("refresh_token");
        editor.remove("user_id");
        editor.apply();
    }

    /**
     * Encrypts a string using the "PBEWithMD5AndDES" algorithm
     *
     * @param encryptionKey Key used to encrypt the access token
     * @param value         String to be encrypted
     * @return Encrypted string
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    private static String encryptString(String encryptionKey, String value) throws GeneralSecurityException, UnsupportedEncodingException {
        final byte[] bytes = value != null ? value.getBytes(UTF8) : new byte[0];
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(encryptionKey.toCharArray()));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        //TODO:
        //pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.ANDROID_ID.getBytes(UTF8), 20));
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.ANDROID_ID.getBytes(UTF8), 20));
        return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), UTF8);
    }

    /**
     * Decrypts a string using the "PBEWithMD5AndDES" algorithm
     *
     * @param encryptionKey Key used to decrypt the access token
     * @param value         String to be decrypted
     * @return Decrypted string
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    private static String decryptString(String encryptionKey, String value) throws GeneralSecurityException, UnsupportedEncodingException {
        final byte[] bytes = value != null ? Base64.decode(value, Base64.DEFAULT) : new byte[0];
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(encryptionKey.toCharArray()));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(Settings.Secure.ANDROID_ID.getBytes(UTF8), 20));
        return new String(pbeCipher.doFinal(bytes), UTF8);
    }
}
