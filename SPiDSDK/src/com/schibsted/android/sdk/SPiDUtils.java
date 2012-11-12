package com.schibsted.android.sdk;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.schibsted.android.sdk.exceptions.SPiDDeviceFingerprintException;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Util class used for generating device fingerprint
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
}
