package com.spid.android.example.nativeapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.response.SPiDResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDConfiguration config = new SPiDConfigurationBuilder(getApplicationContext(),
                null /* The environment you want to run in, stage or production, Norwegian or Swedish */,
                "your-client-id", "your-client-secret", "your-app-url-scheme")
                .debugMode(true)
                .build();
        SPiDClient.getInstance().configure(config);

        Uri data = getIntent().getData();
        final boolean hasClientToken = !SPiDClient.getInstance().isAuthorized() || SPiDClient.getInstance().getAccessToken().isClientToken();
        if(hasClientToken) {
            if(data == null || data.getPath().equals("/login")) {
                FragmentManager fragmentManager = getFragmentManager();
                LoginDialog termsDialog = new LoginDialog();
                termsDialog.show(fragmentManager, "dialog_login");
            } else {
                SPiDLogger.log("Received app redirect to: " + data.getPath());
                SPiDClient.getInstance().handleIntent(data, new LoginListener());
            }
        } else {
            setupContentView();
        }
    }

    protected void setupContentView() {
        setContentView(R.layout.activity_main);

        Button logoutButton = (Button) findViewById(R.id.activity_main_button_logout);
        logoutButton.setOnClickListener(new LogoutButtonListener(this));

        fetchUserInfo();
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
            public void onError(Exception exception) {
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

        TextView userTextView = (TextView) this.findViewById(R.id.activity_main_textview_userinfo);
        userTextView.setText(Html.fromHtml(userInfo));
        userTextView.setVisibility(View.VISIBLE);
    }

    protected class LoginListener implements SPiDAuthorizationListener {

        @Override
        public void onComplete() {
            SPiDLogger.log("Successful login");
            setupContentView();
        }

        @Override
        public void onError(Exception exception) {
            SPiDLogger.log("Error while performing login: " + exception.getMessage());
            FragmentManager fragmentManager = getFragmentManager();
            LoginDialog termsDialog = new LoginDialog();
            termsDialog.show(fragmentManager, "dialog_login");
        }
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        final Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {

                @Override
                public void onComplete() {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error logging out: " + exception.getMessage());
                    Toast.makeText(context, "Error logging out...", Toast.LENGTH_LONG).show();
                    recreate();
                }
            });
        }
    }
}
