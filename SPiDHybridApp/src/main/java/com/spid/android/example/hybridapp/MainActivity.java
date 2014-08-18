package com.spid.android.example.hybridapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.reponse.SPiDResponse;

import org.json.JSONException;

import java.io.IOException;

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

        if (SPiDClient.getInstance().isAuthorized() && !SPiDClient.getInstance().isClientToken()) {
            loginWebView();
        }
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
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupContentView() {
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.activity_main_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/main.html");
        webView.setWebViewClient(new MainWebViewClient());
    }

    protected void loginWebView() {
        final WebView webView = (WebView) findViewById(R.id.activity_main_webview);
        webView.setVisibility(View.GONE);
        webView.loadDataWithBaseURL(null, "<html></html>", "text/html", "utf-8", null);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_main_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        SPiDClient.getInstance().getSessionCode(new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {
                try {
                    String code = result.getJsonObject().getJSONObject("data").getString("code");
                    SPiDLogger.log("Received session code: " + code);

                    invalidateOptionsMenu();

                    String url = SPiDClient.getInstance().getConfig().getServerURL() + "/session/" + code;
                    webView.loadUrl(url);
                } catch (JSONException exception) {
                    onError(exception);
                }

            }

            @Override
            public void onSPiDException(SPiDException exception) {
                onError(exception);
            }

            @Override
            public void onIOException(IOException exception) {
                onError(exception);
            }

            @Override
            public void onException(Exception exception) {
                onError(exception);
            }

            private void onError(Exception exception) {
                SPiDLogger.log("Error logging in to webview: " + exception.getMessage());
                Toast.makeText(getApplicationContext(), "Error logging in to webview", Toast.LENGTH_LONG);
                logout();
            }
        });

    }

    private void logout() {
        SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                SPiDLogger.log("Successful logout");
                logout();
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                SPiDLogger.log("Error logging out: " + exception.getMessage());
                logout();
            }

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("Error logging out: " + exception.getMessage());
                logout();
            }

            @Override
            public void onException(Exception exception) {
                SPiDLogger.log("Error logging out: " + exception.getMessage());
                logout();
            }

            private void logout() {
                setupContentView();
                invalidateOptionsMenu();
            }
        });
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
                logout();
                return true;
            } else if (url.startsWith(serverAccountSummaryUrl)) {
                WebView webView = (WebView) findViewById(R.id.activity_main_webview);
                webView.setVisibility(View.VISIBLE);

                ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_main_progressbar);
                progressBar.setVisibility(View.GONE);
            }

            return false;
        }
    }
}
