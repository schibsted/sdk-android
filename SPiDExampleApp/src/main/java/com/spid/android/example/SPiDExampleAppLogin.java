package com.spid.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.webview.SPiDWebView;
import com.spid.android.sdk.webview.SPiDWebViewClient;

/**
 * Contains the login activity
 */
public class SPiDExampleAppLogin extends Activity {
    protected WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDConfiguration config = new SPiDConfigurationBuilder(getApplicationContext(),
                null /* The environment you want to run in, stage or production, Norwegian or Swedish */,
                "your-client-id", "your-client-secret", "your-app-url-scheme")
                .signSecret("your-secret-sign-key")
                .debugMode(true)
                .build();

        SPiDClient.getInstance().configure(config);

        if (SPiDClient.getInstance().isAuthorized() && !SPiDClient.getInstance().getAccessToken().isClientToken()) {
            SPiDLogger.log("Found access token in SharedPreferences");
            Intent intent = new Intent(this, SPiDExampleAppMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        setupLoginContentView();

        Uri data = getIntent().getData();
        if (data != null && !SPiDClient.getInstance().isAuthorized()) {
            SPiDClient.getInstance().handleIntent(data, new LoginListener(this));
        }
    }

    private void setupLoginContentView() {
        webView = null;

        setContentView(R.layout.login);
        Button loginBrowserButton = (Button) findViewById(R.id.LoginBrowserButton);
        loginBrowserButton.setOnClickListener(new LoginBrowserButtonListener(this));

        Button loginWebViewButton = (Button) findViewById(R.id.LoginWebViewButton);
        loginWebViewButton.setOnClickListener(new LoginWebViewButtonListener(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            setContentView(webView);
        }
    }

    protected class LoginListener implements SPiDAuthorizationListener {
        private final Context context;

        private LoginListener(Context context) {
            this.context = context;
        }

        @Override
        public void onComplete() {
            Intent intent = new Intent(context, SPiDExampleAppMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
//            finish();
        }

        @Override
        public void onError(Exception exception) {
            SPiDLogger.log("Error while performing login: " + exception.getMessage());
            Toast.makeText(context, "Error while performing login", Toast.LENGTH_LONG).show();
            setupLoginContentView();
        }
    }

    protected class SPiDExampleWebViewClient extends SPiDWebViewClient {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            SPiDLogger.log("Started loading page");
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            SPiDLogger.log("Finished loading page");
        }
    }

    protected class LoginBrowserButtonListener implements View.OnClickListener {
        final Context context;

        public LoginBrowserButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            webView = null;
            try {
                SPiDClient.getInstance().browserAuthorization();
            } catch (Exception e) {
                SPiDLogger.log("Error loading webbrowser: " + e.getMessage());
                Toast.makeText(context, "Error loading webbrowser", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected class LoginWebViewButtonListener implements View.OnClickListener {
        final Context context;

        public LoginWebViewButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            try {
                webView = SPiDWebView.webViewAuthorization(context, null, new SPiDExampleWebViewClient(), new LoginListener(context));
                setContentView(webView);
            } catch (Exception e) {
                SPiDLogger.log("Error loading WebView: " + e.getMessage());
                Toast.makeText(context, "Error loading WebView", Toast.LENGTH_LONG).show();
            }
        }
    }
}
