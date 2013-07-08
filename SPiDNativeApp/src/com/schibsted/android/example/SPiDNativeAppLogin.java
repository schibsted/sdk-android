package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.configuration.SPiDConfigurationBuilder;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.request.SPiDUserCredentialTokenRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Contains the login activity
 */
public class SPiDNativeAppLogin extends Activity {

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

        if (SPiDClient.getInstance().isAuthorized() && !SPiDClient.getInstance().isClientToken()) {
            SPiDLogger.log("Found access token in SharedPreferences");
            Intent intent = new Intent(this, SPiDNativeAppMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        setupLoginContentView();

        Uri data = getIntent().getData();
        if (data != null && (!SPiDClient.getInstance().isAuthorized() || SPiDClient.getInstance().isClientToken())) {
            SPiDLogger.log("Received app redirect");
            SPiDClient.getInstance().handleIntent(data, new LoginListener(this));
        }
    }

    private void setupLoginContentView() {
        setContentView(R.layout.login);

        Button loginBrowserButton = (Button) findViewById(R.id.login_button);
        loginBrowserButton.setOnClickListener(new LoginButtonListener(this));

        Button forgotPassword = (Button) findViewById(R.id.forgot_password_button);
        forgotPassword.setOnClickListener(new ForgotPasswordButtonListener(this));

        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new SignupButtonListener(this));
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
            Intent intent = new Intent(context, SPiDNativeAppMain.class);
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

    private class LoginButtonListener implements View.OnClickListener {
        Context context;

        public LoginButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            EditText emailEditText = (EditText) findViewById(R.id.username_edit_text);
            String email = emailEditText.getText().toString();

            EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
            String password = passwordEditText.getText().toString();

            SPiDLogger.log("Email: " + email + " password: " + password);
            SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new LoginListener(context));
            tokenRequest.execute();
        }
    }

    private class ForgotPasswordButtonListener implements View.OnClickListener {
        Context context;

        private ForgotPasswordButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            try {
                SPiDClient.getInstance().browserForgotPassword();
            } catch (UnsupportedEncodingException e) {
                SPiDLogger.log("Error loading webbrowser: " + e.getMessage());
                Toast.makeText(context, "Error loading webbrowser", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SignupButtonListener implements View.OnClickListener {
        Context context;

        private SignupButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            SPiDLogger.log("Switching to signup");
            Intent intent = new Intent(context, SPiDNativeAppSignup.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
