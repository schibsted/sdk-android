package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import com.schibsted.android.sdk.*;
import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/5/12
 * Time: 1:39 PM
 */
public class SPiDExampleAppLogin extends Activity {
    protected WebView webView;

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

        setContentView(R.layout.login);
        Button loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new LoginButtonListener(this));

        Uri data = getIntent().getData();
        if (data != null) {
            SPiDClient.getInstance().handleIntent(data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            setContentView(webView);
        }
    }

    protected class LoginCallback implements SPiDAsyncAuthorizationCallback {
        private Context context;

        private LoginCallback(Context context) {
            this.context = context;
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();

            setContentView(R.layout.login);
            Button loginButton = (Button) findViewById(R.id.LoginButton);
            loginButton.setOnClickListener(new LoginButtonListener(context));
        }

        @Override
        public void onComplete() {
            SPiDLogger.log("Successful login");
            Intent intent = new Intent(context, SPiDExampleAppMain.class);
            startActivity(intent);
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            onError(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            onError(exception);
        }
    }

    protected class LoginButtonListener implements View.OnClickListener {
        Context context;

        public LoginButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            webView = null;
            try {
                webView = SPiDClient.getInstance().getAuthorizationWebView(context, new LoginCallback(context));
                setContentView(webView);
            } catch (Exception e) {
                SPiDLogger.log("Error loading WebView: " + e.getMessage());
                Toast.makeText(context, "Error loading WebView", Toast.LENGTH_LONG).show();
            }
        }
    }
}
