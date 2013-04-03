---
title: Using SPiD API requests
layout: default
---
Using SPiD API requests
===================
Once the app can successfully logged on to SPiD we can start making requests. For example the following code gets the logged in user object.

{% highlight java %}
// Try to fetch the "current user" object
SPiDClient.getInstance().getCurrentUser(new SPiDRequestListener() {
    @Override
    public void onComplete(SPiDResponse result) {
        // Successful request
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

The request returns a `SPiDResponse` object. Before trying to use the message or rawJSON property the client should check for errors using the error property.

There are some wrapper methods as the one above but the you can also write the requests yourself.
For example the following code sends a GET request to /test?user_id=123. Since the SDK takes care of versions the actual path will be ´/api/2/test?user_id=123´. Note that there are both `execute` and `executeAuthorizedRequest` with the difference being that the latter appends a oauthtoken to the request.
{% highlight java %}
SPiDRequest request = new SPiDApiGetRequest("/test/?user_id=" + "123", new SPiDRequestListener() {
    @Override
    public void onComplete(SPiDResponse result) {
        // Successful request
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
request.executeAuthorizedRequest();
{% endhighlight %}

And a example to POST ´user_id=123´ to the endpoint ´/test´.
{% highlight java %}
SPiDRequest request = new SPiDApiPostRequest("/test", new SPiDRequestListener() {
    @Override
    public void onComplete(SPiDResponse result) {
        // Successful request
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
request.addBodyParameter("user_id", "123");
request.execute();
{% endhighlight %}

For more information see the [API reference](api/index.html "API reference").

