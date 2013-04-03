---
title: Getting started with Native
layout: default
---
Native
=========
Following the SPiD 2.7 release (2.7.1 for signup), native app flow is now supported.

The native login flow is implemented using the `SPiDUserCredentialTokenRequest`.
{% highlight java %}
SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new SPiDAuthorizationListener() {
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
{% endhighlight %}

Native signup
=============
The native signup flow is implemented using the `SPiDUser`. To create a SPiD Account a client token is needed, therefor the first step of `createAccountWithEmail` is to acquire a client token and then try to create the SPiD account. 
{% highlight java %}
new SPiDAuthorizationListener {

        @Override
        public void onComplete() {
            // User successfully created
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            if (exception.getDescriptions().containsKey("blocked")) {
                // Handle user blocked
                String errorDescription = exception.getDescriptions().get("blocked");
            } else if (exception.getDescriptions().containsKey("exists")) {
                // Handle user exists
                String errorDescription = exception.getDescriptions().get("exists");
            } else if (exception.getDescriptions().containsKey("email")) {
                // Handle invalid email
                String errorDescription = exception.getDescriptions().get("email");
            } else if (exception.getDescriptions().containsKey("password")) {
                // Handle invalid email
                String errorDescription = exception.getDescriptions().get("password");
            } else {
                // Handle other server exceptions
            }
        }

        @Override
        public void onIOException(IOException exception) {
            // Handle IOException (connection problems)
        }

        @Override
        public void onException(Exception exception) {
            // Handle general Exception (fatal errors, should never happen if SPiDClient is correctly configured)
        }
    }
{% endhighlight %}

Lastly the application need permission to access internet and phone state, the following lines is needed in AndroidManifest.xml
{% highlight xml %}
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
{% endhighlight %}

See the SPiDNativeApp for a simple application with native login and signup.