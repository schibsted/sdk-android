package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.configuration.SPiDConfigurationBuilder;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.request.SPiDFacebookTokenRequest;

import java.io.IOException;
import java.util.Arrays;

/**
 * Contains the login activity
 */
public class SPiDFacebookAppLogin extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("your-client-id")
                .clientSecret("your-client-secret")
                .appURLScheme("your-app-url-scheme")
                .serverURL("your-spidserver-url")
                .context(this)
                .build();

        config.setDebugMode(true);
        SPiDClient.getInstance().configure(config);

        if (SPiDClient.getInstance().isAuthorized()) {
            SPiDLogger.log("Found access token in SharedPreferences");
            Intent intent = new Intent(this, SPiDFacebookAppMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        setupLoginContentView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    private void setupLoginContentView() {
        setContentView(R.layout.login);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("email"));
        authButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                SPiDLogger.log("Callback!");
                if (exception != null) {
                    SPiDLogger.log(exception.getMessage());
                }
                if (session.isOpened()) {
                    SPiDLogger.log("Facebook opened");
                    SPiDConfiguration config = SPiDClient.getInstance().getConfig();
                    SPiDFacebookTokenRequest tokenRequest;
                    try {
                        tokenRequest = new SPiDFacebookTokenRequest(session.getApplicationId(), session.getAccessToken(), session.getExpirationDate(), new LoginListener(config.getContext()));
                        tokenRequest.execute();
                    } catch (SPiDException e) {
                        Toast.makeText(config.getContext(), "Error creating login request", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    protected class LoginListener implements SPiDAuthorizationListener {
        private Context context;

        private LoginListener(Context context) {
            this.context = context;
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
            setupLoginContentView();
        }

        @Override
        public void onComplete() {
            SPiDLogger.log("Successful login");
            Intent intent = new Intent(context, SPiDFacebookAppMain.class);
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
    }
}
