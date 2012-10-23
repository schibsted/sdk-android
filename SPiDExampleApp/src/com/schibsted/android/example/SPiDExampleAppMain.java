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
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/5/12
 * Time: 1:39 PM
 */

public class SPiDExampleAppMain extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button refreshTokenButton = (Button) findViewById(R.id.RefreshTokenButton);
        refreshTokenButton.setOnClickListener(new RefreshTokenButtonListener(this));

        Button oneTimeCodeButton = (Button) findViewById(R.id.OneTimeCodeButton);
        oneTimeCodeButton.setOnClickListener(new OneTimeCodeButtonListener(this));

        Button logoutButton = (Button) findViewById(R.id.LogoutButton);
        logoutButton.setOnClickListener(new LogoutButtonListener(this));

        TextView tokenExpiresTextView = (TextView) findViewById(R.id.TokenExpiresTextView);
        String expiresAt = SPiDClient.getInstance().getTokenExpiresAt().toString();
        tokenExpiresTextView.setText("Token expires at: " + expiresAt);

        getUserName(this);
    }

    private void getUserName(final Context context) {
        SPiDClient.getInstance().getCurrentUser(new SPiDAsyncCallback() {
            @Override
            public void onComplete(SPiDResponse result) {
                TextView userTextView = (TextView) findViewById(R.id.UserTextView);
                String user = "unknown";
                try {
                    user = result.getJsonObject().getJSONObject("data").getString("displayName");

                } catch (JSONException e) {
                    SPiDLogger.log("Error parsing response");
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_LONG).show();
                }
                userTextView.setText("Welcome " + user + "!");
            }

            @Override
            public void onError(Exception exception) {
                SPiDLogger.log("Error getting username");
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
            SPiDClient.getInstance().refreshAccessToken(new SPiDAsyncAuthorizationCallback() {
                @Override
                public void onComplete() {
                    TextView tokenExpiresTextView = (TextView) findViewById(R.id.TokenExpiresTextView);
                    String expiresAt = SPiDClient.getInstance().getTokenExpiresAt().toString();
                    tokenExpiresTextView.setText("Token expires at: " + expiresAt);
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error while refreshing access token");
                    Toast.makeText(context, "Error while refreshing access token", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected class OneTimeCodeButtonListener implements View.OnClickListener {
        Context context;

        public OneTimeCodeButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            /*
            SPiDClient.getInstance().refreshAccessToken(new SPiDAsyncCallback() {
                @Override
                public void onComplete(SPiDResponse result) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error getting one time code");
                    Toast.makeText(context, "Error getting one time code", Toast.LENGTH_LONG).show();
                }
            });
            */
        }
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().logoutSPiDAPI(new SPiDAsyncCallback() {
                @Override
                public void onComplete(SPiDResponse result) {
                    Intent intent = new Intent(context, SPiDExampleAppLogin.class);
                    startActivity(intent);
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(context, "Error logging out...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, SPiDExampleAppLogin.class);
                    startActivity(intent);
                }
            });
        }
    }
}
