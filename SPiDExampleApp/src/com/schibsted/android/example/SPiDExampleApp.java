package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.SPiDConfiguration;
import com.schibsted.android.sdk.SPiDConfigurationBuilder;
import org.scribe.oauth.OAuthService;

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
public class SPiDExampleApp extends Activity {
    private RelativeLayout table;
    private Button loginButton;
    private OAuthService service;

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

        Log.i("SPiD", String.format("SPiD", "Device fingerprint: %s", getDeviceFingerprint()));

        SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("504dffb6efd04b4512000000")
                .clientSecret("iossecret")
                .appURLScheme("sdktest")
                .serverURL("https://stage.payment.schibsted.no")
                .build();

        SPiDClient.getInstance().configure(config);

        setContentView(R.layout.login);

        Log.i("SPiD", "contentview");

        if (getIntent() != null && getIntent().getData() != null) {
            Log.i("SPiD", "Intent: ".concat(getIntent().getData().toString()));
            if (getIntent().getData().toString().startsWith("sdktest://login")) {
                Log.i("SPiD", "getToken");
                //Verifier verifier = new Verifier(getIntent().getData().getQueryParameter("code"));
                Log.i("SPiD", getIntent().getData().getQueryParameter("code"));
                SPiDClient.getInstance().getCode(getIntent().getData());
                SPiDClient.getInstance().getAccessToken();

                //getUserRequestWithCurrentUserAndCompletionHandler
                SPiDClient.getInstance().getCurrentUserRequest();
                //Log.i("SPiD", verifier.getValue());
                //try {
                //Token token = service.getAccessToken(null, verifier);
                //    Log.i("SPiD", token.getRawResponse());
                //} catch (Exception e) {
                //    Log.i("SPiDError", e.getMessage());
                //}
            }
        }

        //Token token = service.getAccessToken(null, new Verifier("2a79ff90ad0a844db98e96805740c242a329ce52"));

        /*
        // TODO: check for accessToken
        if (true) {////!accessToken) {
            final Intent myIntent = new Intent(getApplicationContext(), SPiDLogin.class);
            startActivity(myIntent);
        }
        */

        // get all the view components
        //table = (RelativeLayout) findViewById(R.id.View);

        //loginButton = (Button) findViewById(R.id.LoginButton);
        //if ( ! authInProgress ) {
        //    authInProgress = true;
        /*
        OAuthService service = new ServiceBuilder()
                .provider(SPiDAPI.class)
                .apiKey("504dffb6efd04b4512000000")
                .apiSecret("iossecret")
                .callback("sdktest://login")
                .build();

        Log.i("SPiD","=== SPiD's OAuth Workflow ===");

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(service.getAuthorizationUrl(null))));
        */
        // Obtain the Request Token
        /*
        Log.i("SPiD","Fetching the Request Token...");
        Token requestToken;
        Verifier verifier;
        requestToken = service.getRequestToken();
        Log.i("SPiD","Got the Request Token!");

        Log.i("SPiD","Now go and authorize Scribe here:");
        Log.i("SPiD",service.getAuthorizationUrl(requestToken));

        Toast.makeText(this, "Please authorize " + getString(R.string.app_name), Toast.LENGTH_LONG).show();

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(service.getAuthorizationUrl(requestToken))));
*/
    }

    public void loginToSPiD(View view) {
        Log.i("SPiD", SPiDClient.getInstance().getAuthorizationURL());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDClient.getInstance().getAuthorizationURL())));
    }

}
