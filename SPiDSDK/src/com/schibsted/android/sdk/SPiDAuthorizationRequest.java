package com.schibsted.android.sdk;

import android.net.Uri;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 9:20 PM
 */

/*
conn.setRequestProperty("User-Agent", System.getProperties().
                getProperty("http.agent") + " FacebookAndroidSDK");
 */
public class SPiDAuthorizationRequest {
    private String code = "";

    private SPiDAsyncTaskCompleteListener<Void> callback;

    public SPiDAuthorizationRequest(SPiDAsyncTaskCompleteListener<Void> authorizationCallback) {
        this.callback = authorizationCallback;
    }

    public void getAccessToken(String code) {
        //isEmptyString(config.getCode(), "No code available");
        // TODO: prevent multiple calls

        SPiDConfiguration config = SPiDClient.getInstance().getConfig();
        SPiDRequest request = new SPiDRequest("POST", "https://stage.payment.schibsted.no/oauth/token", new AccessTokenCallback(callback));
        request.addBodyParameter("grant_type", "authorization_code");
        request.addBodyParameter("client_id", config.getClientID());
        request.addBodyParameter("client_secret", config.getClientSecret());
        request.addBodyParameter("code", code);
        request.addBodyParameter("redirect_uri", config.getRedirectURL() + "login");
        request.execute();
    }

    public boolean handleIntent(Uri data) {
        if (data.toString().startsWith("sdktest://login")) {
            code = data.getQueryParameter("code");
            if (code.length() > 0) {
                getAccessToken(code);
                return true;
            } else {

            }
        }
        return false;
    }
/*
    public void startLoginActivity(SPiDConfiguration config) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SPiDClient.getInstance().getAuthorizationURL())));
    }
*/

    class AccessTokenCallback implements SPiDAsyncTaskCompleteListener<SPiDResponse> {
        private SPiDAsyncTaskCompleteListener<Void> callback;

        public AccessTokenCallback(SPiDAsyncTaskCompleteListener<Void> callback) {
            this.callback = callback;
        }

        @Override
        public void onComplete(SPiDResponse result) {
            SPiDClient.getInstance().setAccessToken(new SPiDAccessToken(result.getJsonObject()));
            callback.onComplete((Void) null);
        }

        @Override
        public void onError(SPiDResponse result) {
            Log.i("SPiD", "Error in access token request");
            // TODO: Create a nice error
            callback.onError((Void) null);
        }
    }
}
