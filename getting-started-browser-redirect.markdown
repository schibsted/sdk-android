---
title: Getting started with browser redirect
layout: default
---
Safari redirect
===============
The authorization process for browser redirect is in two steps, in the first we login to SPiD in browser and then receives a code. If the user already has logged into SPiD, browser has a cookie that will login the user and redirect back to the app.
The second step is to exchange the code for a access token which can be used to make requests against SPiD.
Both these steps are done automatically and the client will only have to use the method `authorizationWithBrowser` method as seen below.

{% highlight java %}
try {
    SPiDClient.getInstance().authorizationWithBrowser();
} catch (UnsupportedEncodingException exception) {
    // Handle problem encoding url, likely to be a configuration issue
}
{% endhighlight %}

When the login completes in the browser the following code the was setup in "Setting up SPiD" will be called
{% highlight java %}
Uri data = getIntent().getData();
if (data != null && !SPiDClient.getInstance().isAuthorized()) {
    SPiDClient.getInstance().handleIntent(data, new SPiDAuthorizationListener() {
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
}
{% endhighlight %}

To be able to receive browser redirects a android scheme must be configured in the AndroidManifest.xml, also the application need permission to access internet and phone state
{% highlight xml %}
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<application ...>
    <activity ...>
        <intent-filter>
            ...
            <data android:scheme="your-app-url-scheme"/>
        </intent-filter>
    </activity>	
</application>
{% endhighlight %}

This will complete the login and call the listener. You can then start making [API requests](using-spid-requests.html "API requests").




