package com.spid.android.sdk.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import com.spid.android.sdk.exceptions.SPiDDeviceFingerprintException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Helper class for various methods
 */
public class SPiDUtils {

    /**
     * Generates a unique device fingerprint
     *
     * @param context Android application context
     * @return The device fingerprint
     */
    public static String getDeviceFingerprint(Context context) {
        UUID uuid;

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number
        try {
            // 9774d56d682e549c is used in multiple devices due to a bug android 2.2, should not affect later versions
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if (deviceId != null) {
                    uuid = UUID.nameUUIDFromBytes(deviceId.getBytes("utf8"));
                } else {
                    uuid = UUID.randomUUID();
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new SPiDDeviceFingerprintException("Error generating device fingerprint", e);
        }

        // TODO: Should store in SharedPreferences?
        return uuid.toString();
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
}
