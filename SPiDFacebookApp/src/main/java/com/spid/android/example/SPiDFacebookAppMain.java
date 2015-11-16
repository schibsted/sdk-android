package com.spid.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.exceptions.SPiDInvalidAccessTokenException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.response.SPiDResponse;

import org.json.JSONException;

public class SPiDFacebookAppMain extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new LogoutButtonListener(this));

        getUserName(this);
    }

    private void getUserName(final Context context) {
        SPiDClient.getInstance().getCurrentUser(new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {
                TextView userTextView = (TextView) findViewById(R.id.user_text_view);
                String user = "unknown";
                try {
                    user = result.getJsonObject().getJSONObject("data").getString("displayName");
                } catch (JSONException e) {
                    SPiDLogger.log("Error getting username");
                    Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
                }
                userTextView.setText("Welcome " + user + "!");
            }

            @Override
            public void onError(Exception exception) {
                if (exception instanceof SPiDInvalidAccessTokenException) {
                    SPiDLogger.log("Token expired or invalid: " + exception.getMessage());
                    Intent intent = new Intent(SPiDClient.getInstance().getConfig().getContext(), SPiDFacebookAppLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    SPiDLogger.log("Error getting username: " + exception.getMessage());
                    Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        final Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    SPiDClient.getInstance().clearAccessToken();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(context, SPiDFacebookAppLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error logging out: " + exception.getMessage());
                    Toast.makeText(context, "Error logging out...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
