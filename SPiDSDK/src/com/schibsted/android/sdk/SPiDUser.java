package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.reponse.SPiDResponse;
import com.schibsted.android.sdk.request.SPiDApiPostRequest;
import com.schibsted.android.sdk.request.SPiDClientTokenRequest;
import com.schibsted.android.sdk.request.SPiDRequest;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 3/21/13
 * Time: 2:16 PM
 */
public class SPiDUser {
    private String email;
    private String password;
    private SPiDAuthorizationListener authorizationListener;

    public SPiDUser(String email, String password, SPiDAuthorizationListener authorizationListener) {
        this.email = email;
        this.password = password;
        this.authorizationListener = authorizationListener;
    }

    public void createAccount() {
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        if (token == null || !token.isClientToken()) {
            SPiDLogger.log("Requesting client token!");
            SPiDClientTokenRequest clientTokenRequest = new SPiDClientTokenRequest(new ClientTokenListener());
            clientTokenRequest.execute();
        } else {
            createUser();
        }
    }

    private void createUser() {
        SPiDRequest signupRequest = getSignupRequest(email, password);
        signupRequest.executeAuthorizedRequest();

    }

    private boolean validateEmail() {
        return true;
    }

    private SPiDRequest getSignupRequest(String email, String password) {
        String redirectUri = SPiDClient.getInstance().getConfig().getRedirectURL();
        SPiDRequest signupRequest = new SPiDApiPostRequest("/signup", new CreateUserListener());
        signupRequest.addBodyParameter("email", email);
        signupRequest.addBodyParameter("password", password);
        signupRequest.addBodyParameter("redirectUri", redirectUri);
        return signupRequest;
    }

    private class ClientTokenListener implements SPiDAuthorizationListener {

        @Override
        public void onComplete() {
            SPiDLogger.log("Got client token, trying to create user");
            createUser();
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

    private class CreateUserListener implements SPiDRequestListener {

        @Override
        public void onComplete(SPiDResponse result) {
            SPiDLogger.log(result.getBody());
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