package com.schibsted.android.example.hybridapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.configuration.SPiDConfigurationBuilder;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidAccessTokenException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.reponse.SPiDResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

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
        webView.setWebViewClient(getDialog_login());
    }

    // TODO: should be private class
    private WebViewClient getDialog_login() {
        return new WebViewClient() {
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
        };
    }

    private void fetchUserInfo() {
        SPiDClient.getInstance().getCurrentUser(new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {
                try {
                    String userInfo = generateUserInfo(result.getJsonObject().getJSONObject("data"));
                    setUserInfo(userInfo);
                } catch (JSONException e) {
                    SPiDLogger.log("Error getting user info:" + e.getMessage());
                    setUserInfo("Error fetching user information");
                }
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                if (exception instanceof SPiDInvalidAccessTokenException) {
                    SPiDClient.getInstance().clearAccessToken();
                    Toast.makeText(getApplicationContext(), "Session expired, please login again", Toast.LENGTH_LONG).show();
                    recreate();
                } else {
                    SPiDLogger.log("Error getting username: " + exception.getMessage());
                    setUserInfo("Error fetching user information");
                }
            }

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                setUserInfo("Error fetching user information");
            }

            @Override
            public void onException(Exception exception) {
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                setUserInfo("Error fetching user information");
            }
        });
    }

    private String generateUserInfo(JSONObject userData) throws JSONException {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append("<b>Display name</b>");
        userInfo.append("<br>");
        userInfo.append(userData.getString("displayName"));
        userInfo.append("<br><br>");
        userInfo.append("<b>User id</b>");
        userInfo.append("<br>");
        userInfo.append(userData.getString("userId"));
        userInfo.append("<br><br>");
        userInfo.append("<b>Emails</b>");
        userInfo.append("<br>");
        JSONArray emails = userData.getJSONArray("emails");
        for (int i = 0; i < emails.length(); i++) {
            userInfo.append(emails.getJSONObject(i).getString("value"));
            userInfo.append("<br>");
        }
        userInfo.append("<br>");
        userInfo.append("<b>Accounts</b>");
        userInfo.append("<br>");
        JSONObject accounts = userData.getJSONObject("accounts");
        Iterator<?> accountKeys = accounts.keys();
        while (accountKeys.hasNext()) {
            String key = (String) accountKeys.next();
            userInfo.append(((JSONObject) accounts.get(key)).getString("accountName"));
            userInfo.append("<br>");
        }

        return userInfo.toString();
    }

    private void setUserInfo(String userInfo) {
        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.activity_main_progressbar);
        progressBar.setVisibility(View.GONE);

        /*TextView userTextView = (TextView) this.findViewById(R.id.activity_main_textview_userinfo);
        userTextView.setText(Html.fromHtml(userInfo));
        userTextView.setVisibility(View.VISIBLE);*/
    }

    protected class LoginListener implements SPiDAuthorizationListener {

        private void onError(Exception exception) {
            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            FragmentManager fragmentManager = getFragmentManager();
            LoginDialog termsDialog = new LoginDialog();
            termsDialog.show(fragmentManager, "dialog_login");
        }

        @Override
        public void onComplete() {
            SPiDLogger.log("Successful login");
            setupContentView();
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
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    recreate();
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
            });
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error logging out: " + exception.getMessage());
            Toast.makeText(context, "Error logging out...", Toast.LENGTH_LONG).show();
            recreate();
        }
    }
}
