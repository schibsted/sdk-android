package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Session;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidAccessTokenException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.reponse.SPiDResponse;
import org.json.JSONException;

import java.io.IOException;

/**
 * Contains the main window activity
 */

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
            public void onSPiDException(SPiDException exception) {
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

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
            }
        });
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
                    if (Session.getActiveSession() != null) {
                        Session.getActiveSession().closeAndClearTokenInformation();
                    }
                    Intent intent = new Intent(context, SPiDFacebookAppLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
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
        }
    }
}
