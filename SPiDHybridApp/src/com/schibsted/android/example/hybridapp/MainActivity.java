package com.schibsted.android.example.hybridapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.configuration.SPiDConfigurationBuilder;
import com.schibsted.android.sdk.logger.SPiDLogger;

/**
 * Contains the activity_main window activity
 */

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("your-client-id")
                .clientSecret("your-client-secret")
                .appURLScheme("your-app-url-scheme")
                .serverURL("your-spidserver-url")
                .signSecret("your-secret-sign-key")
                .context(this)
                .build();

        config.setDebugMode(true);
        SPiDClient.getInstance().configure(config);

        setupContentView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (SPiDClient.getInstance().isAuthorized() && !SPiDClient.getInstance().isClientToken()) {
            MenuItem loginItem = menu.findItem(R.id.menu_item_login);
            loginItem.setVisible(false);

            MenuItem logoutItem = menu.findItem(R.id.menu_item_logout);
            logoutItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_login:
                FragmentManager fragmentManager = getFragmentManager();
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(fragmentManager, "dialog_login");
                return true;
            case R.id.menu_item_logout:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setupContentView() {
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.activity_main_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/main.html");
        webView.setWebViewClient(new MainWebViewClient());
    }

    private class MainWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String serverUrl = SPiDClient.getInstance().getConfig().getServerURL();
            String serverLoginUrl = serverUrl + "/auth/login";
            String serverLogoutUrl = serverUrl + "/logout";
            String serverAccountSummaryUrl = serverUrl + "/account/summary";

            SPiDLogger.log("Loading: " + url);
            if (url.startsWith(serverLoginUrl)) {
                SPiDLogger.log("Intercepted SPiD login page");
                FragmentManager fragmentManager = getFragmentManager();
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(fragmentManager, "dialog_login");
                return true;
            } else if (url.startsWith(serverLogoutUrl)) {
                SPiDLogger.log("Intercepted SPiD logout page");
                return true;
            } else if (url.startsWith(serverAccountSummaryUrl)) {
                SPiDLogger.log("Intercepted SPiD Account summary page");
                return true;
            }

            return false;
        }
    }
}
