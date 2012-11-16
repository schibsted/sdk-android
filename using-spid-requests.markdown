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
        // Handle SPiDException
    }
    @Override
    public void onIOException(IOException exception) {
        // Handle IOException
    }
});
{% endhighlight %}

The request returns a `SPiDResponse` object. Before trying to use the message or rawJSON property the client should check for errors using the error property.

There are some wrapper methods as the one above but the you can also write the requests yourself.
For example the following code sends a GET request to /test?user_id=123. Since the SDK takes care of versions the actual path will be ´/api/2/test?user_id=123´
{% highlight java %}
SPiDRequest request = SPiDClient.getInstance().apiGetRequest("/test/?user_id=" + "123", new SPiDRequestListener() {
    @Override
    public void onComplete(SPiDResponse result) {
        // Successful request
    }
    @Override
    public void onSPiDException(SPiDException exception) {
        // Handle SPiDException
    }
    @Override
    public void onIOException(IOException exception) {
        // Handle IOException
    }
});
request.execute();
{% endhighlight %}

And a example to POST ´user_id=123´ to the endpoint ´/test´.
{% highlight java %}
SPiDRequest request = SPiDClient.getInstance().apiPostRequest("/test", new SPiDRequestListener() {
    @Override
    public void onComplete(SPiDResponse result) {
        // Successful request
    }
    @Override
    public void onSPiDException(SPiDException exception) {
        // Handle SPiDException
    }
    @Override
    public void onIOException(IOException exception) {
        // Handle IOException
    }
});
request.addBodyParameter("user_id", "123");
request.execute();
{% endhighlight %}

For more information see the [API reference](api/index.html "API reference").

