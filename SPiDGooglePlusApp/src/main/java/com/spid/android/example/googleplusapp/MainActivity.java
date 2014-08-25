package com.spid.android.example.googleplusapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Contains the activity_main window activity
 */

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    public static final String GOOGLE_PLUS_SCOPES = Scopes.PLUS_LOGIN + " " + "email";

    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;
    private boolean intentInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup SPiD
        SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("your-client-id")
                .clientSecret("your-client-secret")
                .appURLScheme("your-app-url-scheme")
                .serverURL("your-spidserver-url")
                .signSecret("your-secret-sign-key")
                .context(getApplicationContext())
                .build();
        config.setDebugMode(true);
        SPiDClient.getInstance().configure(config);

        // Check if GooglePlayService is available, if not we could hide the login button
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Error getting username", Toast.LENGTH_LONG).show();
        }

        // Setup GooglePlus, the email scope is needed for SPiD login with Google Plus
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope("email"))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Handle app redirects
        Uri data = getIntent().getData();
        if (data != null && (!SPiDClient.getInstance().isAuthorized() || SPiDClient.getInstance().isClientToken())) {
            SPiDLogger.log("This app does not have app redirects, this should not happen...");
            Toast.makeText(this, "This app does not have app redirects, this should not happen...", Toast.LENGTH_LONG).show();
        }

        // Setup view
        setupContentView();
    }

    // Layout setup
    public void setupContentView() {
        if (!SPiDClient.getInstance().isAuthorized() || SPiDClient.getInstance().isClientToken()) {
            setupEmptyContentView();
        } else {
            setupMainContentView();
        }
    }

    private void setupMainContentView() {
        setContentView(R.layout.activity_main);

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFromSPiD();
            }
        });

        getUserNameFromSPiD(this);
    }

    private void setupEmptyContentView() {
        setContentView(R.layout.activity_empty_main);

        FragmentManager fragmentManager = getFragmentManager();
        GooglePlusLoginDialog googlePlusLoginDialog = new GooglePlusLoginDialog();
        googlePlusLoginDialog.show(fragmentManager, "dialog_login");
    }

    // Handle Google Plus connection
    @Override
    public void onConnected(Bundle bundle) {
        SPiDLogger.log("Successfully connected to Google+");
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(String token) {
                SPiDLogger.log("Google plus token received:" + token);
                trySPiDLoginWithGooglePlusToken(token);
            }
        });
    }

    // Handle lost Google Plus connection
    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    // Handle failed Google Plus connection
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);

            } catch (IntentSender.SendIntentException e) {
                googleApiClient.disconnect();
                googleApiClient.connect();
            }
        }
    }

    // Handle result if user intervention was required for authentication
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            intentInProgress = false;

            if (resultCode == RESULT_OK) {
                SPiDLogger.log("User approved authorization, continuing with login");
                if (!googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
            } else {
                dismissLoadingDialog();
                SPiDLogger.log("Authorization denied by user");
                Toast.makeText(this, "Authorization denied by user", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void trySPiDLoginWithGooglePlusToken(String token) {
        SPiDGooglePlusTokenRequest tokenRequest = new SPiDGooglePlusTokenRequest(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                SPiDLogger.log("SPiD logging successful, access token received: " + SPiDClient.getInstance().getAccessToken().getAccessToken());
                dismissLoadingDialog();
                setupContentView();
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                // Handle user does not exist
                if (exception instanceof SPiDUnknownUserException) {
                    dismissLoadingDialog();
                    showNoAccountDialog();
                } else {
                    onError("SPiDException when logging in with Google Plus token: " + exception.getMessage());
                }
            }

            @Override
            public void onIOException(IOException exception) {
                onError("IOException when logging in with Google Plus token: " + exception.getMessage());
            }

            @Override
            public void onException(Exception exception) {
                onError("Exception when logging in with Google Plus token: " + exception.getMessage());
            }

            private void onError(String error) {
                dismissLoadingDialog();
                SPiDLogger.log(error);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
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
                    SPiDLogger.log("Trying token GooglePlus access token");
                    token = GoogleAuthUtil.getToken(activity, Plus.AccountApi.getAccountName(googleApiClient), "oauth2:" + MainActivity.GOOGLE_PLUS_SCOPES);
                } catch (IOException transientException) {
                    // Network or server error, try later
                    SPiDLogger.log("IOException when requesting Google Plus token: " + transientException.toString());
                } catch (UserRecoverableAuthException userRecoverableAuthException) {
                    // This means that the app hasn't been authorized by the user for access to the scope, so we're going to have
                    // to fire off the (provided) Intent to arrange for that, this should only be done once
                    // and should not be a problem since we use the same scope as in plus login

                    SPiDLogger.log("UserRecoverableAuthException received: " + userRecoverableAuthException.toString());
                    Intent recover = userRecoverableAuthException.getIntent();
                    startActivityForResult(recover, MainActivity.REQUEST_CODE_RESOLVE_ERR);
                } catch (GoogleAuthException authException) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    SPiDLogger.log(authException.toString());
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null && token.length() > 0) {
                    googleTokenListener.onComplete(token);
                } else {
                    SPiDLogger.log("No token received from Google");
                    Toast.makeText(MainActivity.this, "No access token received from Google Plus", Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
    }

    public void connectToGooglePlus() {
        showLoadingDialog();
        googleApiClient.connect();
    }

    public void logoutFromGooglePlus() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void getUserNameFromSPiD(final Context context) {
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
                    setupContentView();
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
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(final String token) {
                signupWithGooglePlusToken(token);
            }
        });
    }

    private void signupWithGooglePlusToken(final String token) {
        SPiDUser.signupWithGooglePlus(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                trySPiDLoginWithGooglePlusToken(token);
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                onError("SPiDException when creating user from Google Plus token: " + exception.getMessage(), exception);
            }

            @Override
            public void onIOException(IOException exception) {
                onError("IOException when creating user from Google Plus token: " + exception.getMessage(), exception);
            }

            @Override
            public void onException(Exception exception) {
                onError("Exception when creating user from Google Plus token: " + exception.getMessage(), exception);
            }

            private void onError(String error, Exception exception) {
                dismissLoadingDialog();
                SPiDLogger.log(error, exception);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void attachGooglePlusUser() {
        getGooglePlusToken(new GoogleTokenListener() {
            @Override
            public void onComplete(final String token) {
                attachGooglePlusAccountWithToken(token);
            }
        });
    }

    private void attachGooglePlusAccountWithToken(String token) {
        SPiDUser.attachGooglePlusAccount(getPackageName(), token, new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();

                Toast.makeText(MainActivity.this, "Google Plus account successfully attached", Toast.LENGTH_LONG).show();
                SPiDLogger.log("Google Plus account successfully attached");

                setupContentView();
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                SPiDLogger.log("SPiDException when attaching Google Plus account: " + exception.getMessage());
            }

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("IOException when attaching Google Plus account: " + exception.getMessage());
            }

            @Override
            public void onException(Exception exception) {
                SPiDLogger.log("Exception when attaching Google Plus account: " + exception.getMessage());
            }
        });
    }

    public interface GoogleTokenListener {
        public void onComplete(String token);
    }

    public void logoutFromSPiD() {
        SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
            @Override
            public void onComplete() {
                logoutFromGooglePlus();
                setupContentView();
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                onError("SPiDException when logging out from SPiD: " + exception.getMessage());
            }

            @Override
            public void onIOException(IOException exception) {
                onError("IOException when logging out from SPiD: " + exception.getMessage());
            }

            @Override
            public void onException(Exception exception) {
                onError("Exception when logging out from SPiD: " + exception.getMessage());
            }

            private void onError(String exception) {
                SPiDClient.getInstance().clearAccessTokenAndWaitingRequests();
                SPiDLogger.log(exception);
                Toast.makeText(MainActivity.this, exception, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
        }
        progressDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
