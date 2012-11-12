package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.schibsted.android.sdk.*;
import com.schibsted.android.sdk.exceptions.SPiDException;
import org.json.JSONException;

import java.io.IOException;

/**
 * Contains the main window activity
 */

public class SPiDExampleAppMain extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button refreshTokenButton = (Button) findViewById(R.id.refreshTokenButton);
        refreshTokenButton.setOnClickListener(new RefreshTokenButtonListener(this));

        Button oneTimeCodeButton = (Button) findViewById(R.id.oneTimeCodeButton);
        oneTimeCodeButton.setOnClickListener(new OneTimeCodeButtonListener(this));

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new LogoutButtonListener(this));

        TextView tokenExpiresTextView = (TextView) findViewById(R.id.tokenExpiresTextView);
        String expiresAt = SPiDClient.getInstance().getTokenExpiresAt().toString();
        tokenExpiresTextView.setText("Token expires at: " + expiresAt);

        getUserName(this);
    }

    private void getUserName(final Context context) {
        SPiDClient.getInstance().getCurrentUser(new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {
                TextView userTextView = (TextView) findViewById(R.id.userTextView);
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
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("Error getting username: " + exception.getMessage());
                Toast.makeText(context, "Error getting username", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected class RefreshTokenButtonListener implements View.OnClickListener {
        Context context;

        public RefreshTokenButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().refreshAccessToken(new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    TextView tokenExpiresTextView = (TextView) findViewById(R.id.tokenExpiresTextView);
                    String expiresAt = SPiDClient.getInstance().getTokenExpiresAt().toString();
                    tokenExpiresTextView.setText("Token expires at: " + expiresAt);
                }

                @Override
                public void onSPiDException(SPiDException exception) {
                    onError(exception);
                }

                @Override
                public void onIOException(IOException exception) {
                    onError(exception);
                }
            });
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error while refreshing access token: " + exception.getMessage());
            Toast.makeText(context, "Error while refreshing access token", Toast.LENGTH_LONG).show();
        }
    }

    protected class OneTimeCodeButtonListener implements View.OnClickListener {
        Context context;

        public OneTimeCodeButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().getOneTimeCode(new SPiDRequestListener() {
                @Override
                public void onComplete(SPiDResponse result) {
                    String oneTimeCode = "none";
                    try {
                        oneTimeCode = result.getJsonObject().getJSONObject("data").getString("code");
                    } catch (JSONException e) {
                        SPiDLogger.log("Error getting one time code");
                        Toast.makeText(context, "Error getting one time code", Toast.LENGTH_LONG).show();
                    }
                    TextView oneTimeCodeTextView = (TextView) findViewById(R.id.oneTimeCodeTextView);
                    oneTimeCodeTextView.setText("One time code: " + oneTimeCode);
                }

                @Override
                public void onSPiDException(SPiDException exception) {
                    onError(exception);
                }

                @Override
                public void onIOException(IOException exception) {
                    onError(exception);
                }
            });
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error getting one time code: " + exception.getMessage());
            Toast.makeText(context, "Error getting one time code", Toast.LENGTH_LONG).show();
        }
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().logoutSPiDAPI(new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    Intent intent = new Intent(context, SPiDExampleAppLogin.class);
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
            });
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error logging out: " + exception.getMessage());
            Toast.makeText(context, "Error logging out...", Toast.LENGTH_LONG).show();
        }
    }
}
