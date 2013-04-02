package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.user.SPiDUser;

import java.io.IOException;

/**
 * Contains the login activity
 */
public class SPiDNativeAppSignup extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupSignupContentView();
    }

    private void setupSignupContentView() {
        setContentView(R.layout.signup);
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new SignupButtonListener(this));

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new LoginButtonListener(this));
    }

    protected class SignupListener implements SPiDAuthorizationListener {
        private Context context;

        private SignupListener(Context context) {
            this.context = context;
        }

        private void onError(Exception exception) {
            SPiDLogger.log("Error while preforming signup: " + exception.getMessage());
            Toast.makeText(context, "Error while preforming signup", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete() {
            Toast.makeText(context, "User created, please check your email for verification", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, SPiDNativeAppLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            if (exception.getDescriptions().containsKey("blocked")) {
                String message = exception.getDescriptions().get("blocked");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("exists")) {
                String message = exception.getDescriptions().get("exists");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("email")) {
                String message = exception.getDescriptions().get("email");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("password")) {
                String message = exception.getDescriptions().get("password");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else {
                onError(exception);
            }
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

    protected class LoginButtonListener implements View.OnClickListener {
        private Context context;

        public LoginButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDLogger.log("Switching to login");
            Intent intent = new Intent(context, SPiDNativeAppLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private class SignupButtonListener implements View.OnClickListener {
        private Context context;

        private SignupButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            EditText emailEditText = (EditText) findViewById(R.id.username_edit_text);
            String email = emailEditText.getText().toString();

            EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
            String password = passwordEditText.getText().toString();

            SPiDLogger.log("Email: " + email + " password: " + password);
            SPiDUser user = new SPiDUser(email, password, new SignupListener(context));
            user.createAccount();
        }
    }
}
