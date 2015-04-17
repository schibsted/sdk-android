package com.spid.android.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.logger.SPiDLogger;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper class for various methods
 */
public final class SPiDUtils {

    public static final String HMAC_SHA_2561 = "HmacSHA256";

    private static String sID = null;

    public static final String DEVICE_ID = "DEVICE_ID";

    private SPiDUtils() {
    }

    /**
     * Returns a unique id for this device
     *
     * @return The device fingerprint
     */
    public static String getDeviceFingerprint() {
        return readId();
    }

    /**
     * Encodes a string with Base64
     *
     * @param input String to be encoded
     * @return Base64 encoded input
     */
    public static String encodeBase64(String input) {
        byte[] data;
        try {
            data = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is always present in Android, should never occur
            SPiDLogger.log("Failed to getBytes for UTF-8", e);
            return "";
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Converts a byte[] to a hex string
     *
     * @param byteArray Array to be converted
     * @return Array as a hex string
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : byteArray) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    /**
     * Generated a hmac sha256 for a string
     *
     * @param key    Key used for hash generation
     * @param input  String to be hashed
     * @throws Exception The exception thrown if any
     * @return Hashed string
     */
    public static String getHmacSHA256(String key, String input) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA_2561);
        mac.init(new SecretKeySpec(key.getBytes(), HMAC_SHA_2561));
        byte[] bs = mac.doFinal(input.getBytes());
        return byteArrayToHexString(bs);
    }

    private synchronized static String readId() {
        if (sID == null) {
            sID = readDeviceId();
            if (sID == null) {
                sID = generateDeviceId();
            }
        }
        return sID;
    }

    private static String readDeviceId() {
        SharedPreferences secure = getSecurePreferencesFile();
        return secure.getString(DEVICE_ID, null);
    }

    private static String generateDeviceId() {
        String id = UUID.randomUUID().toString();
        SharedPreferences secure = getSecurePreferencesFile();
        SharedPreferences.Editor editor = secure.edit();
        editor.putString(DEVICE_ID, id);
        editor.apply();
        return id;
    }

    /**
     * Returns the shared preferences in private mode
     *
     * @return The private shared preferences
     */
    public static SharedPreferences getSecurePreferencesFile() {
        Context context = SPiDClient.getInstance().getConfig().getContext();
        return context.getSharedPreferences(context.getPackageName() + ".sdk", Context.MODE_PRIVATE);
    }
}
