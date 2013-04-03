package com.schibsted.android.sdk.user;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.accesstoken.SPiDAccessToken;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.listener.SPiDAuthorizationListener;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.reponse.SPiDResponse;
import com.schibsted.android.sdk.request.SPiDApiPostRequest;
import com.schibsted.android.sdk.request.SPiDClientTokenRequest;
import com.schibsted.android.sdk.request.SPiDRequest;

import java.io.IOException;

/**
 * Contains methods to create a new SPiD user
 */
public class SPiDUser {
    private String email;
    private String password;
    private SPiDAuthorizationListener authorizationListener;

    /**
     * Constructor
     *
     * @param email                 Username
     * @param password              Password
     * @param authorizationListener Listener called on completion or failure, can be <code>null</code>
     */
    public SPiDUser(String email, String password, SPiDAuthorizationListener authorizationListener) {
        this.email = email;
        this.password = password;
        this.authorizationListener = authorizationListener;
    }

    /**
     * Creates a SPiD user account with the specified credentials, acquires a client token if needed
     */
    public void createAccount() {
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        if (token == null || !token.isClientToken()) {
            SPiDLogger.log("Requesting client token!");
            SPiDClientTokenRequest clientTokenRequest = new SPiDClientTokenRequest(new ClientTokenListener());
            clientTokenRequest.execute();
        } else {
            runSignupRequest();
        }
    }

    /**
     * Creates and runs a SPiD signup request
     */
    private void runSignupRequest() {
        SPiDRequest signupRequest = getSignupRequest(email, password);
        signupRequest.executeAuthorizedRequest();

    }

    /**
     * Creates a SPiD signup request
     *
     * @param email    Email
     * @param password Password
     * @return The signup request
     */
    private SPiDRequest getSignupRequest(String email, String password) {
        String redirectUri = SPiDClient.getInstance().getConfig().getRedirectURL();
        SPiDRequest signupRequest = new SPiDApiPostRequest("/signup", new CreateUserListener());
        signupRequest.addBodyParameter("email", email);
        signupRequest.addBodyParameter("password", password);
        signupRequest.addBodyParameter("redirectUri", redirectUri);
        return signupRequest;
    }

    /**
     * Listener for when client token has been acquired
     */
    private class ClientTokenListener implements SPiDAuthorizationListener {

        @Override
        public void onComplete() {
            SPiDLogger.log("Got client token, trying to create user");
            runSignupRequest();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            authorizationListener.onSPiDException(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            authorizationListener.onIOException(exception);
        }

        @Override
        public void onException(Exception exception) {
            authorizationListener.onException(exception);
        }
    }

    /**
     * Listener for when user has been created
     */
    private class CreateUserListener implements SPiDRequestListener {

        @Override
        public void onComplete(SPiDResponse result) {
            authorizationListener.onComplete();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            authorizationListener.onSPiDException(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            authorizationListener.onIOException(exception);
        }

        @Override
        public void onException(Exception exception) {
            authorizationListener.onException(exception);
        }
    }
}