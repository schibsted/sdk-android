package com.spid.android.example.googleplusapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.spid.android.example.R;
import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.SPiDConfiguration;
import com.spid.android.sdk.configuration.SPiDConfigurationBuilder;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.exceptions.SPiDInvalidAccessTokenException;
import com.spid.android.sdk.exceptions.SPiDUnknownUserException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.reponse.SPiDResponse;
import com.spid.android.sdk.request.SPiDGooglePlusTokenRequest;
import com.spid.android.sdk.user.SPiDUser;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    public static final String GOOGLE_PLUS_SCOPES = Scopes.PLUS_LOGIN + " email";
    public static final String OAUTH_EXCEPTION = "OAuthException";

    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient = null;
    private boolean intentInProgress;

    private ConnectionResult mConnectionResult;

    SignInButton signInButton;
    TextView userText;
    Button logoutButton;

    private boolean signInClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMainContentView();

        // Setup SPiD
        if (SPiDClient.getInstance().getConfig() == null) {
            SPiDConfiguration config = new SPiDConfigurationBuilder()
                    .clientID("your-client-id")
                    .clientSecret("your-client-secret")
                    .appURLScheme("your-app-url-scheme")
                    .serverURL("your-spidserver-url")
                    .signSecret("your-secret-sign-key")
                    .debugMode(true)
                    .context(getApplicationContext())
                    .build();
            SPiDClient.getInstance().configure(config);
        }

        // Setup GooglePlus, the email scope is needed for SPiD login with Google Plus
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addScope(new Scope("email"))
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
            SPiDLogger.log("Google client was not connecting, connecting now");
            googleApiClient.connect();
        }

        checkGooglePlusConnection();
    }

    private void checkGooglePlusConnection() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Toast.makeText(this, getString(R.string.failedGoogleConnect), Toast.LENGTH_LONG).show();
        }
    }

    private void setupMainContentView() {
        setContentView(R.layout.activity_main);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        userText = (TextView) findViewById(R.id.user_text_view);
        logoutButton = (Button) findViewById(R.id.logout_button);

        signInButton.setOnClickListener(this);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFromSPiD();
                signOutFromGplus();
                displayLoginScreen(true);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        SPiDLogger.log("Connected to Google");
        signInClicked = false;
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(String token) {
                // attempt to connect to SPiD, if the user is unknown we will give the option to attach the user to
                // an existing one or create a new one associated with this Google account
                trySPiDLoginWithGooglePlusToken(token, false);
            }
        });
    }

    // Handle lost Google Plus connection
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
        displayLoginScreen(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    // Handle failed Google Plus connection
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress && connectionResult.hasResolution()) {
            mConnectionResult = connectionResult;
            if (signInClicked) {
                intentInProgress = true;
                try {
                    connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    SPiDLogger.log("SendIntentException", e);
                    googleApiClient.reconnect();
                }
            }
        }
    }

    // Handle result if user intervention was required for authentication
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (resultCode != RESULT_OK) {
                signInClicked = false;
            }
            intentInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    SPiDLogger.log("User approved authorization, continue with login");
                    googleApiClient.connect();
                }
            } else {
                SPiDLogger.log("Authorization denied by user");
                Toast.makeText(this, getString(R.string.authorizationDeniedUser), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void trySPiDLoginWithGooglePlusToken(final String token, final boolean isRetryAttempted) {
        final SPiDGooglePlusTokenRequest tokenRequest = new SPiDGooglePlusTokenRequest(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                SPiDLogger.log("SPiD log in successful, access token received: " + SPiDClient.getInstance().getAccessToken().getAccessToken());
                displayLoginScreen(false);
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                handleError(exception);
            }

            @Override
            public void onIOException(IOException exception) {
                handleError(exception);
            }

            @Override
            public void onException(Exception exception) {
                handleError(exception);
            }


            private void handleError(Exception exception) {
                // Handle user does not exist
                if (exception instanceof SPiDUnknownUserException) {
                    SPiDLogger.log("SPiDUnknownUserException while trying to log in", exception);

                    // either the user didn't exist or the permission was revoked and we're using an old token,
                    // invalidate token and try one once more before showing no account dialog. This is mostly for
                    // debugging reasons when revoking permission and using an old token. Should not be used in production code
                    SPiDUnknownUserException spidUnknownUserException = (SPiDUnknownUserException) exception;
                    if (spidUnknownUserException.getErrorType().equals(OAUTH_EXCEPTION) && !isRetryAttempted) {
                        SPiDLogger.log("Permission possibly revoked, invalidating token and trying again");
                        GoogleAuthUtil.invalidateToken(MainActivity.this, token);
                        getGooglePlusToken(new GoogleTokenListener() {
                            @Override
                            public void onComplete(String token) {
                                trySPiDLoginWithGooglePlusToken(token, true);
                            }
                        });
                    } else {
                        showNoAccountDialog();
                    }

                } else {
                    SPiDLogger.log("Unexpected exception when logging in: " + exception.getMessage(), exception);
                    Toast.makeText(MainActivity.this, getString(R.string.loginError), Toast.LENGTH_LONG).show();
                }
            }
        });
        tokenRequest.execute();
    }

    // Prompt user if they want to create a new user or attach to a existing one
    private void showNoAccountDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        NoAccountDialog noAccountDialog = new NoAccountDialog();
        noAccountDialog.show(fragmentManager, "dialog_no_account");
    }

    // Fetch a Google Plus token to be sent to SPiD
    private void getGooglePlusToken(final GoogleTokenListener googleTokenListener) {
        final Activity activity = this;
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    token = GoogleAuthUtil.getToken(activity, Plus.AccountApi.getAccountName(googleApiClient), "oauth2:" + MainActivity.GOOGLE_PLUS_SCOPES);
                } catch (IOException transientException) {
                    // Network or server error, try later
                    SPiDLogger.log("IOException when requesting Google Plus token: " + transientException.toString(), transientException);
                } catch (UserRecoverableAuthException userRecoverableAuthException) {
                    // This means that the app hasn't been authorized by the user for access to the scope, so we're going to have
                    // to fire off the (provided) Intent to arrange for that, this should only be done once
                    // and should not be a problem since we use the same scope as in plus login

                    SPiDLogger.log("UserRecoverableAuthException received: " + userRecoverableAuthException.toString(),
                            userRecoverableAuthException);
                    SPiDLogger.log("Intent in progress: " + intentInProgress);
                    if (!intentInProgress) {
                        intentInProgress = true;
                        Intent recover = userRecoverableAuthException.getIntent();
                        startActivityForResult(recover, MainActivity.REQUEST_CODE_RESOLVE_ERR);
                    }
                } catch (GoogleAuthException authException) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    SPiDLogger.log(authException.toString(), authException);
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if (!TextUtils.isEmpty(token)) {
                    googleTokenListener.onComplete(token);
                } else {
                    SPiDLogger.log("No token received from Google");
                    Toast.makeText(MainActivity.this, getString(R.string.googleAccesTokenError), Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
    }

    public void connectToGooglePlus() {
        if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    public void logoutFromGooglePlus() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void updateUsernameFromSPiD(final Context context) {
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
                    SPiDClient.getInstance().clearAccessTokenAndWaitingRequests();
                    SPiDLogger.log("Token expired or invalid: " + exception.getMessage());
                    displayLoginScreen(true);
                } else {
                    onError("SPiDException when fetching user information: " + exception.getMessage());
                }
            }

            @Override
            public void onIOException(IOException exception) {
                onError("IOException when fetching user information: " + exception.getMessage());
            }

            @Override
            public void onException(Exception exception) {
                onError("Exception when fetching user information: " + exception.getMessage());
            }

            private void onError(String error) {
                SPiDLogger.log(error);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createUserFromGooglePlus() {
        SPiDLogger.log("Creating user from Google+ account...");
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(final String token) {
                signupWithGooglePlusToken(token);
            }
        });
    }

    private void signupWithGooglePlusToken(final String token) {
        SPiDLogger.log("Signing up with Google+ token...");
        SPiDUser.signupWithGooglePlus(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                trySPiDLoginWithGooglePlusToken(token, false);
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                handleError(exception);
            }

            @Override
            public void onIOException(IOException exception) {
                handleError(exception);
            }

            @Override
            public void onException(Exception exception) {
                handleError(exception);
            }

            private void handleError(Exception exception) {
                SPiDLogger.log("Exception when attempting to creating user from Google Plus token: " + exception.getMessage(), exception);
                Toast.makeText(MainActivity.this, getString(R.string.failedCreateUser), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void attachGooglePlusUser() {
        SPiDLogger.log("Attaching Google+ user...");
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(final String token) {
                attachGooglePlusAccountWithToken(token);
            }
        });
    }

    private void attachGooglePlusAccountWithToken(final String token) {
        SPiDUser.attachGooglePlusAccount(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                trySPiDLoginWithGooglePlusToken(token, false);
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                handleError(exception);
            }

            @Override
            public void onIOException(IOException exception) {
                handleError(exception);
            }

            @Override
            public void onException(Exception exception) {
                handleError(exception);
            }

            private void handleError(Exception exception) {
                SPiDLogger.log("Exception when attaching Google Plus account: " + exception.getMessage(), exception);
                Toast.makeText(MainActivity.this, getString(R.string.failedAttachGoogle), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
        } else {
            SPiDLogger.log("Google + was connecting");
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        SPiDLogger.log("Connection status: " + googleApiClient.isConnected() + " " + googleApiClient.isConnecting());
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                SPiDLogger.log("Exception during startResolutionForResult", e);
                intentInProgress = false;
                googleApiClient.connect();
            }
        } else {
            SPiDLogger.log("Google+ has no resolution");
            googleApiClient.connect();
        }
    }

    private void signOutFromGplus() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }
    }

    public void logoutFromSPiD() {
        SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                SPiDLogger.log("Logged out from SPiD");
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                handleError(exception);
            }

            @Override
            public void onIOException(IOException exception) {
                handleError(exception);
            }

            @Override
            public void onException(Exception exception) {
                handleError(exception);
            }

            private void handleError(Exception exception) {
                SPiDClient.getInstance().clearAccessTokenAndWaitingRequests();
                SPiDLogger.log("Exception when logging out from SPiD: " + exception.getMessage(), exception);
                Toast.makeText(MainActivity.this, getString(R.string.logoutError), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.loading));
        }
        progressDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void displayLoginScreen(boolean displayLogin) {
        if (displayLogin) {
            userText.setText(getString(R.string.welcomeUnknown));
            signInButton.setVisibility(View.VISIBLE);
            userText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        } else {
            if (SPiDClient.getInstance().getAccessToken() != null) {
                updateUsernameFromSPiD(this);
            }
            signInButton.setVisibility(View.GONE);
            userText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    public interface GoogleTokenListener {
        public void onComplete(String token);
    }
}