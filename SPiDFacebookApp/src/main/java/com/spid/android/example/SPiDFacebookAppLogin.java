package com.spid.android.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.exceptions.SPiDUnknownUserException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDFacebookTokenRequest;
import com.spid.android.sdk.user.SPiDUser;

import java.util.Arrays;

/**
 * Contains the login activity
 */
public class SPiDFacebookAppLogin extends Activity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SPiDConfiguration config = new SPiDConfigurationBuilder(getApplicationContext(),
                null /* The environment you want to run in, stage or production, Norwegian or Swedish */,
                "your-client-id", "your-client-secret", "your-app-url-scheme")
                .signSecret("your-sign-secret")
                .debugMode(true)
                .build();
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
        setContentView(R.layout.facebook_login);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("email"));
        authButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    SPiDLogger.log("Exception: " + exception.getMessage());
                }
                if (session.isOpened()) {
                    SPiDLogger.log("Facebook opened");
                    loginWithFacebookAccount();
                }
            }
        });

        TextView termsLink = (TextView) findViewById(R.id.terms_link);
        termsLink.setOnClickListener(new TermsButtonListener(this));
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

    private void loginWithFacebookAccount() {
        showLoadingDialog();
        Session session = Session.getActiveSession();
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDFacebookTokenRequest tokenRequest;
        try {
            tokenRequest = new SPiDFacebookTokenRequest(session.getApplicationId(), session.getAccessToken(), session.getExpirationDate(), new LoginListener());
            tokenRequest.execute();
        } catch (SPiDException e) {
            dismissLoadingDialog();
            Toast.makeText(config.getContext(), "Error creating login request", Toast.LENGTH_LONG).show();
        }
    }

    private void showNoExistingUserDialog() {
        Context context = SPiDClient.getInstance().getConfig().getContext();

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.no_account_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("User does not exist");
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button createNewUser = (Button) view.findViewById(R.id.createNewUser);
        Button attachFacebookUser = (Button) view.findViewById(R.id.attachFacebookUser);
        Button cancelDialog = (Button) view.findViewById(R.id.cancelDialog);

        createNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showLoadingDialog();
                createSPiDUserFromFacebookAccount();
            }
        });
        attachFacebookUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showNativeLogin();
            }
        });
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Session.getActiveSession() != null) {
                    Session.getActiveSession().closeAndClearTokenInformation();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showNativeLogin() {
        SPiDLogger.log("Switch to native login");
        Context context = SPiDClient.getInstance().getConfig().getContext();
        Intent intent = new Intent(context, SPiDNativeLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void createSPiDUserFromFacebookAccount() {
        SPiDLogger.log("Trying to create new SPiD account from Facebook account");
        final Context context = SPiDClient.getInstance().getConfig().getContext();
        Session facebookSession = Session.getActiveSession();
        SPiDUser.signupWithFacebook(facebookSession.getApplicationId(), facebookSession.getAccessToken(), facebookSession.getExpirationDate(), new SPiDAuthorizationListener() {

            @Override
            public void onComplete() {
                dismissLoadingDialog();
                SPiDLogger.log("New SPiD account successfully created, trying to login");
                loginWithFacebookAccount();
            }

            @Override
            public void onError(Exception exception) {
                dismissLoadingDialog();
                SPiDLogger.log("Error while performing login: " + exception.getMessage());
                Toast.makeText(context, "Error while performing login", Toast.LENGTH_LONG).show();
                setupLoginContentView();
            }
        });
    }

    protected class LoginListener implements SPiDAuthorizationListener {
        private final Context context;

        private LoginListener() {
            this.context = SPiDClient.getInstance().getConfig().getContext();
        }

        @Override
        public void onComplete() {
            dismissLoadingDialog();
            SPiDLogger.log("Successful login");
            Intent intent = new Intent(context, SPiDFacebookAppMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Exception exception) {
            if (exception instanceof SPiDUnknownUserException) {
                showNoExistingUserDialog();
            } else {
                dismissLoadingDialog();
                SPiDLogger.log("Error while performing login: " + exception.getMessage());
                Toast.makeText(context, "Error while performing login", Toast.LENGTH_LONG).show();
                setupLoginContentView();
            }
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
