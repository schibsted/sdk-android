package com.spid.android.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.spid.android.sdk.SPiDClient;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper class for various methods
 */
public final class SPiDUtils {

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
     * @param string String to be encoded
     * @return Base64 encoded string
     * @throws UnsupportedEncodingException
     */
    public static String encodeBase64(String string) throws UnsupportedEncodingException {
        byte[] data = string.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Converts a byte[] to a hex string
     *
     * @param array Array to be converted
     * @return Array as a hex string
     */
    public static String byteArrayToHexString(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    /**
     * Generated a hmac sha256 for a string
     *
     * @param key    Key used for hash generation
     * @param string String to be hashed
     * @return Hashed string
     * @throws Exception
     */
    public static String getHmacSHA256(String key, String string) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        byte[] bs = mac.doFinal(string.getBytes());
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
