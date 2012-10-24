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
public class SPiDExampleAppLogin extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDLogger.log(String.format("Device fingerprint: %s", SPiDUtils.getDeviceFingerprint(this)));

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
