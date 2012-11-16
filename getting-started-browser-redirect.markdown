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
    SPiDClient.getInstance().handleIntent(data, new LoginListener());
}
{% endhighlight %}

This will complete the login and call the listener. You can then start making [API requests](using-spid-requests.html "API requests").




