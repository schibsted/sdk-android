---
title: Setting up SPiD
layout: default
---
Setting up SPiD
===============
This will provide a small introduction on how to setup the SPiD SDK for your iOS application.

The all SDK interactions is centered around `SPiDClient` which is a singleton. All client calls should go through this class.

The SDK needs to be setup with parameters received from SPiD which are _client ID_ and _client secret_ and _SPiD server_. The SDK also need to know the app URL scheme.
App URL Scheme and SPiD server are used for generating requests and redirect URLs, they can be overridden by using the properties of `SPiDClient` if needed.
The setup should be done after the app has loaded, preferably in `onCreate` method. The app also needs to take Androids garbage collection into account, there
is no guarantee that the singleton will persist a app suspension.

The following code is used to setup the `SPiDClient`.
{% highlight java %}
SPiDConfiguration config = new SPiDConfigurationBuilder()
        .clientID("your-client-id")
        .clientSecret("your-client-secret")
        .appURLScheme("your-app-url-scheme")
        .serverURL("your-spidserver-url")
        .context(this)
        .build();
SPiDClient.getInstance().configure(config);
{% endhighlight %}

After setup of the `SPiDClient` the application can check if there are any access token saved in the keychain or if authorization is required. This can be done with the following code.
{% highlight java %}
if (SPiDClient.getInstance().isAuthorized()) {
    // Access token was saved in keychain
} else {
    // Not logged in
}
{% endhighlight %}

Since there are times when there are redirects to the app this must be handled. One example is opening the app from a browser.
This is done by calling the the `handleIntent` method of the `SPiDClient` as shown below.

{% highlight java %}
Uri data = getIntent().getData();
if (data != null && !SPiDClient.getInstance().isAuthorized()) {
    SPiDClient.getInstance().handleIntent(data, new LoginListener());
}
{% endhighlight %}

As the code shows this needs a `LoginListener` which is a implementation of `SPiDAuthorizationListener`, an example is seen below.

{% highlight java %}
protected class LoginListener implements SPiDAuthorizationListener {
    private LoginListener() {
        super();
    }

    @Override
    public void onComplete() {
        // Successful login
    }

    @Override
    public void onSPiDException(SPiDException exception) {
        // Handle SPiDException
    }

    @Override
    public void onIOException(IOException exception) {
        // Handle IOException
    }
}
{% endhighlight %}

The actual authorization can be done with either [browser redirect](getting-started-browser-redirect.html "browser redirect") or [WebView](getting-started-webview.html "WebView").