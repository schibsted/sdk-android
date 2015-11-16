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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.exceptions.SPiDUnknownUserException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDFacebookTokenRequest;
import com.spid.android.sdk.user.SPiDUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains the login activity
 */
public class SPiDFacebookAppLogin extends Activity {

    private static final String TAG = SPiDFacebookAppLogin.class.getSimpleName();
    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;

    private LoginResult loginResult;

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

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @return Date one hour in the future
     */
    private static Date getOneHourInTheFuture() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();
    }

    private void setupLoginContentView() {
        setContentView(R.layout.facebook_login);

        LoginButton loginButton = (LoginButton) findViewById(R.id.authButton);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SPiDFacebookAppLogin.this.loginResult = loginResult;
                SPiDLogger.log("User granted access [" + (loginResult.getAccessToken() != null ? loginResult.getAccessToken() : "null") + "]");

                loginWithFacebookAccount();
            }

            @Override
            public void onCancel() {
                SPiDLogger.log("User cancelled login attempt");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getBaseContext(), "Could not connect to your Facebook account", Toast.LENGTH_LONG).show();
                SPiDLogger.log("An error occurred while trying to get connect to Facebook: " + exception.getMessage());
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
        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        try {
            SPiDFacebookTokenRequest tokenRequest = new SPiDFacebookTokenRequest(getString(R.string.facebook_app_id), loginResult.getAccessToken().getToken(), getOneHourInTheFuture(), new LoginListener());
            tokenRequest.execute();
        } catch (SPiDException e) {
            Toast.makeText(config.getContext(), "Error creating login request", Toast.LENGTH_LONG).show();
        } finally {
            dismissLoadingDialog();
        }
    }

    private void showNoExistingUserDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.no_account_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SPiDFacebookAppLogin.this);
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

        String facebookToken = loginResult != null ? loginResult.getAccessToken().getToken() : null;
        if (facebookToken != null) {
            SPiDUser.signupWithFacebook(getString(R.string.facebook_app_id), facebookToken, getOneHourInTheFuture(), new SPiDAuthorizationListener() {

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
            SPiDLogger.log("Error while logging in...");
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
