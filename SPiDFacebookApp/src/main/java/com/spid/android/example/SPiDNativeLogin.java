package com.spid.android.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Session;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDUserCredentialTokenRequest;
import com.spid.android.sdk.user.SPiDUser;

import java.io.IOException;

/**
 * Contains the login activity
 */
public class SPiDNativeLogin extends Activity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLoginContentView();
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
        }
        progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void setupLoginContentView() {
        setContentView(R.layout.native_login);

        Button loginBrowserButton = (Button) findViewById(R.id.login_button);
        loginBrowserButton.setOnClickListener(new LoginButtonListener(this));

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelButtonListener(this));

        TextView termsLink = (TextView) findViewById(R.id.terms_link);
        termsLink.setOnClickListener(new TermsButtonListener(this));
    }

    protected class LoginListener implements SPiDAuthorizationListener {
        private final Context context;

        private LoginListener(Context context) {
            this.context = context;
        }

        @Override
        public void onComplete() {
            SPiDLogger.log("Successful login");
            Session session = Session.getActiveSession();
            SPiDUser.attachFacebookAccount(session.getApplicationId(), session.getAccessToken(), session.getExpirationDate(), new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    dismissLoadingDialog();
                    Intent intent = new Intent(context, SPiDFacebookAppMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onSPiDException(SPiDException exception) {
                    dismissLoadingDialog();
                    SPiDLogger.log("Error while preforming login: " + exception.getError());
                    Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
                    setupLoginContentView();
                }

                @Override
                public void onIOException(IOException exception) {
                    dismissLoadingDialog();
                    SPiDLogger.log("Error while preforming login: " + exception.getMessage());
                    Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
                    setupLoginContentView();
                }

                @Override
                public void onException(Exception exception) {
                    dismissLoadingDialog();
                    SPiDLogger.log("Error while preforming login: " + exception.getMessage());
                    Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
                    setupLoginContentView();
                }
            });
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            dismissLoadingDialog();
            SPiDLogger.log("Error while preforming login: " + exception.getError());
            Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
            setupLoginContentView();
        }

        @Override
        public void onIOException(IOException exception) {
            dismissLoadingDialog();
            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
            setupLoginContentView();
        }

        @Override
        public void onException(Exception exception) {
            dismissLoadingDialog();
            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            Toast.makeText(context, "Error while preforming login", Toast.LENGTH_LONG).show();
            setupLoginContentView();
        }
    }

    private class LoginButtonListener implements View.OnClickListener {
        final Context context;

        public LoginButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            showLoadingDialog();
            EditText emailEditText = (EditText) findViewById(R.id.username_edit_text);
            String email = emailEditText.getText().toString();

            EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
            String password = passwordEditText.getText().toString();

            SPiDLogger.log("Email: " + email + " password: " + password);
            SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new LoginListener(context));
            tokenRequest.execute();
        }
    }

    private class CancelButtonListener implements View.OnClickListener {
        private final Context context;

        public CancelButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            if (Session.getActiveSession() != null) {
                Session.getActiveSession().closeAndClearTokenInformation();
            }
            Intent intent = new Intent(context, SPiDFacebookAppLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private class TermsButtonListener implements View.OnClickListener {
        private final Activity activity;

        private TermsButtonListener(Activity context) {
            this.activity = context;
        }

        @Override
        public void onClick(View view) {
            SPiDTermsDialog.showTerms(activity);
        }
    }
}
