package com.spid.android.sdk.user;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.configuration.TokenType;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.jwt.Audience;
import com.spid.android.sdk.jwt.SPiDJwt;
import com.spid.android.sdk.jwt.SubjectClaim;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDApiPostRequest;
import com.spid.android.sdk.request.SPiDClientTokenRequest;
import com.spid.android.sdk.request.SPiDRequest;
import com.spid.android.sdk.response.SPiDResponse;
import com.spid.android.sdk.utils.SPiDUrl;

import java.util.Calendar;
import java.util.Date;

/**
 * Contains methods to create a new SPiD user
 */
public final class SPiDUser {

    private SPiDUser() {}

    /**
     * Creates a SPiD user account with the specified credentials, acquires a client token if needed
     *
     * @param email                 Email to register
     * @param password              Password
     * @param authorizationListener Callback listener
     */
    public static void signupWithCredentials(final String email, final String password, final SPiDAuthorizationListener authorizationListener) {
        if(!hasClientToken()) {
            SPiDClientTokenRequest clientTokenRequest = new SPiDClientTokenRequest(new SPiDAuthorizationListener() {

                @Override
                public void onComplete() {
                    SPiDRequest signupRequest = createSignupRequest(email, password, authorizationListener);
                    signupRequest.executeAuthorizedRequest();
                }

                @Override
                public void onError(Exception exception) {
                    authorizationListener.onError(exception);
                }
            });
            clientTokenRequest.execute();
        } else {
            SPiDRequest signupRequest = createSignupRequest(email, password, authorizationListener);
            signupRequest.executeAuthorizedRequest();
        }
    }

    /**
     * Create user from facebook token
     *
     * @param appId                 Facebook application id
     * @param facebookToken         Facebook token
     * @param expirationDate        Facebook token expiration date
     * @param authorizationListener Callback listener
     */
    public static void signupWithFacebook(final String appId, final String facebookToken, final Date expirationDate, final SPiDAuthorizationListener authorizationListener) {
        if(!hasClientToken()) {
            SPiDLogger.log("Requesting client token!");
            SPiDClientTokenRequest clientTokenRequest = new SPiDClientTokenRequest(new SPiDAuthorizationListener() {
                @Override
                public void onComplete() {
                    SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.REGISTRATION, Audience.SIGN_UP.toString(), expirationDate, TokenType.FACEBOOK, facebookToken);
                    SPiDRequest signupRequest = new SPiDApiPostRequest("/signup_jwt", new AuthorizationRequestListener(authorizationListener));
                    signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
                    signupRequest.executeAuthorizedRequest();
                }

                @Override
                public void onError(Exception exception) {
                    authorizationListener.onError(exception);
                }
            });
            clientTokenRequest.execute();
        } else {
            SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.REGISTRATION, Audience.SIGN_UP.toString(), expirationDate, TokenType.FACEBOOK, facebookToken);
            SPiDRequest signupRequest = new SPiDApiPostRequest("/signup_jwt", new AuthorizationRequestListener(authorizationListener));
            signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
            signupRequest.executeAuthorizedRequest();
        }
    }

    /**
     * Create user from Google plus token
     * Note that since google does not return expire date its hardcoded to one hour
     * This should not be a problem since a fresh token should always be sent in to this function
     *
     * @param appId                 Google plus client id
     * @param googlePlusToken         Google plus token
     * @param authorizationListener Callback listener
     */
    public static void signupWithGooglePlus(final String appId, final String googlePlusToken, final SPiDAuthorizationListener authorizationListener) {
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        final Date expirationDate = getOneHourInTheFuture();
        if (token == null || !token.isClientToken()) {
            SPiDClientTokenRequest clientTokenRequest = new SPiDClientTokenRequest(new SPiDAuthorizationListener() {

                @Override
                public void onComplete() {
                    SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.REGISTRATION, Audience.SIGN_UP.toString(), expirationDate, TokenType.GOOGLE_PLUS, googlePlusToken);
                    SPiDRequest signupRequest = new SPiDApiPostRequest("/signup_jwt", new AuthorizationRequestListener(authorizationListener));
                    signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
                    signupRequest.executeAuthorizedRequest();
                }

                @Override
                public void onError(Exception exception) {
                    authorizationListener.onError(exception);
                }
            });
            clientTokenRequest.execute();
        } else {
            SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.REGISTRATION, Audience.SIGN_UP.toString(), expirationDate, TokenType.GOOGLE_PLUS, googlePlusToken);
            SPiDRequest signupRequest = new SPiDApiPostRequest("/signup_jwt", new AuthorizationRequestListener(authorizationListener));
            signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
            signupRequest.executeAuthorizedRequest();
        }
    }

    /**
     * Attaches a Facebook account to the current user
     * Note that since google does not return expire date its hardcoded to one hour
     * This should not be a problem since a fresh token should always be sent in to this function
     *
     * @param appId                 Google plus client id
     * @param googlePlusToken         Google plus token
     * @param authorizationListener Callback listener
     */
    public static void attachGooglePlusAccount(final String appId, final String googlePlusToken, final SPiDAuthorizationListener authorizationListener) {
        Date expirationDate = getOneHourInTheFuture();
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        if (token != null && !token.isClientToken()) { // Check for user token
            SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.REGISTRATION, Audience.SIGN_UP.toString(), expirationDate, TokenType.GOOGLE_PLUS, googlePlusToken);
            SPiDRequest signupRequest = new SPiDApiPostRequest("/user/attach_jwt", new AuthorizationRequestListener(authorizationListener));
            signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
            signupRequest.executeAuthorizedRequest();
        } else {
            authorizationListener.onError(new SPiDException("User token needed to attach Google plus account"));
        }
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

    /**
     * Attaches a Google plus account to the current user
     *
     * @param appId                 Facebook application id
     * @param facebookToken         Facebook token
     * @param expirationDate        Facebook token expiration date
     * @param authorizationListener Callback listener
     */
    public static void attachFacebookAccount(final String appId, final String facebookToken, final Date expirationDate, final SPiDAuthorizationListener authorizationListener) {
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        if (token != null && !token.isClientToken()) { // Check for user  token
                    SPiDJwt jwt = new SPiDJwt(appId, SubjectClaim.ATTACH, Audience.ATTACH.toString(), expirationDate, TokenType.FACEBOOK, facebookToken);
            SPiDRequest signupRequest = new SPiDApiPostRequest("/user/attach_jwt", new AuthorizationRequestListener(authorizationListener));
            signupRequest.addBodyParameter("jwt", jwt.encodedJwtString());
            signupRequest.executeAuthorizedRequest();
        } else {
            authorizationListener.onError(new SPiDException("User token needed to attach Facebook account"));
        }
    }

    private static boolean hasClientToken() {
        SPiDAccessToken token = SPiDClient.getInstance().getAccessToken();
        return token != null && token.isClientToken();
    }

    /**
     * Creates a SPiD signup request
     *
     * @param email    Email
     * @param password Password
     * @return The signup request
     */
    private static SPiDRequest createSignupRequest(String email, String password, SPiDAuthorizationListener authorizationListener) {
        String redirectUri = SPiDUrl.getAuthorizationURL();
        SPiDRequest signupRequest = new SPiDApiPostRequest("/signup", new AuthorizationRequestListener(authorizationListener));
        signupRequest.addBodyParameter("email", email);
        signupRequest.addBodyParameter("password", password);
        signupRequest.addBodyParameter("redirectUri", redirectUri);
        return signupRequest;
    }

    /**
     * Wrapper that handles the SPiDResponse
     */
    private static class AuthorizationRequestListener implements SPiDRequestListener {

        final SPiDAuthorizationListener listener;

        private AuthorizationRequestListener(SPiDAuthorizationListener authorizationListener) {
            listener = authorizationListener;
        }

        @Override
        public void onComplete(SPiDResponse result) {
            listener.onComplete();
        }

        @Override
        public void onError(Exception exception) {
            listener.onError(exception);
        }
    }
}