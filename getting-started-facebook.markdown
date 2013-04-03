---
title: Getting started with Facebook
layout: default
---
Setting up Facebook
===================
Following the SPiD 2.7 release, login and signup through Facebook is now supported.
The SPiD SDK uses the Facebook SDK to enable login. The currently supported version of the Facebook SDK is 3.2.
This also requires a FacebookApp to be setup for your application.

For information about downloading the SDK, adding it to the project and creating a Facebook app see [Facebook getting started with Android](https://developers.facebook.com/docs/getting-started/facebook-sdk-for-android/3.0/ "Facebook getting started with Android").

The next step is to add the login action and handler which is described in detail at [Facebook authentication](https://developers.facebook.com/docs/tutorials/androidsdk/3.0/scrumptious/authenticate/ "Facebook authentication").

To use the facebook sdk the `LoginActivity` and `ApplicationId` should be added to AndroidManifest.xml, the application need permission to access internet and phone state.
{% highlight xml %}
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<application ...>
    <activity android:name="com.facebook.LoginActivity"/>
    <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="your-facebook-app-id"/>
</application>
{% endhighlight %}

Then we define the callback for when the WebView or Facebook app finishes. This should be added in your main activity.
{% highlight java %}
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
}
{% endhighlight %}

Next we need to add the actual login call, this example uses a simple `LoginButton` from the facebook sdk. On successfully facebook login the token is passed to the SPiD SDK for SPiD login.
{% highlight java %}
LoginButton authButton = (LoginButton) findViewById(R.id.authButton); // Get button from layout
authButton.setReadPermissions(Arrays.asList("email")); // email is needed by SPiD
authButton.setSessionStatusCallback(new Session.StatusCallback() {

    @Override
    public void call(Session session, SessionState state, Exception exception) {
        if (exception != null) {
            // Handle exception from Facebook
        }
        if (session.isOpened()) {
            SPiDConfiguration config = SPiDClient.getInstance().getConfig();
            SPiDFacebookTokenRequest tokenRequest;
            try {
                tokenRequest = new SPiDFacebookTokenRequest(session.getApplicationId(), session.getAccessToken(), session.getExpirationDate(), new SPiDAuthorizationListener() {
                    @Override
                    public void onComplete() {
                        // Successful login
                    }

                    @Override
                    public void onSPiDException(SPiDException exception) {
                        // Handle SPiDException (server errors)
                    }

                    @Override
                    public void onIOException(IOException exception) {
                        // Handle IOException (connection problems)
                    }

                    @Override
                    public void onException(Exception exception) {
                        // Handle general Exception (fatal errors, should never happen if SPiDClient is correctly configured)
                    }
                });
                tokenRequest.execute();
            } catch (SPiDException e) {
                // Handle SPiD error(most likely configuration issues)
            }
        }
    }
});
{% endhighlight %}

Also be sure to logout from both facebook and SPiD.
{% highlight java %}
SPiDClient.getInstance().apiLogout(new SPiDAuthorizationListener() {
    @Override
    public void onComplete() {
        Session.getActiveSession().closeAndClearTokenInformation(); // Remove facebook token
        // Logout complete
    }

    @Override
    public void onSPiDException(SPiDException exception) {
        // Handle SPiDException (server errors)
    }

    @Override
    public void onIOException(IOException exception) {
        // Handle IOException (connection problems)
    }

    @Override
    public void onException(Exception exception) {
        // Handle general Exception (fatal errors, should never happen if SPiDClient is correctly configured)
    }
});
{% endhighlight %}

See the SPiDFacebookApp in the SDK for a simple application with facebook login.
