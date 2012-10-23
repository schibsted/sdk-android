package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import com.schibsted.android.sdk.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/5/12
 * Time: 1:39 PM
 */

/*
    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

    final String tmDevice, tmSerial, androidId;
    tmDevice = "" + tm.getDeviceId();
    tmSerial = "" + tm.getSimSerialNumber();
    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);



    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    String deviceId = deviceUuid.toString();

    advertisementID for iphone
 */
public class SPiDExampleAppLogin extends Activity {

    private String getDeviceFingerprint() {
        UUID uuid;

        final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number
        // TODO: Should store this number somewhere....
        try {
            // 9774d56d682e549c is used in multiple devices due to a bug android 2.2, should not affect later versions
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if (deviceId != null) {
                    uuid = UUID.nameUUIDFromBytes(deviceId.getBytes("utf8"));
                } else {
                    uuid = UUID.randomUUID();
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uuid.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDLogger.log(String.format("Device fingerprint: %s", getDeviceFingerprint()));

        SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("your-client-id")
                .clientSecret("your-client-secret")
                .appURLScheme("your-app-url-scheme")
                .serverURL("your-spidserver-url")
                .context(this)
                .build();

        SPiDClient.getInstance().configure(config);

        if (SPiDClient.getInstance().isAuthorized()) {
            SPiDLogger.log("Found access token in SharedPreferences");
            Intent intent = new Intent(this, SPiDExampleAppMain.class);
            startActivity(intent);
        }

        // TODO: change id to lower_case_with_underscore
        setContentView(R.layout.login);
        Button loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new LoginButtonListener(this));
    }

    protected class LoginButtonListener implements View.OnClickListener {
        Context context;

        public LoginButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDLogger.log("onClick");

            WebView webView = SPiDClient.getInstance().getAuthorizationWebView(context, new SPiDAsyncAuthorizationCallback() {
                @Override
                public void onComplete() {
                    SPiDLogger.log("Successful login");
                    Intent intent = new Intent(context, SPiDExampleAppMain.class);
                    startActivity(intent);
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error while preforming login");
                    Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();

                    setContentView(R.layout.login);
                    Button loginButton = (Button) findViewById(R.id.LoginButton);
                    loginButton.setOnClickListener(new LoginButtonListener(context));
                }
            });
            setContentView(webView);
        }
    }
}
