---
title: Using SPiD API requests
layout: default
---
Using SPiD API requests
===================
Once the app can successfully logged on to SPiD we can start making requests. For example the following code gets the logged in user object.

{% highlight objectivec %}
// Try to fetch the "current user" object
[[SPiDClient sharedInstance] getUserRequestWithCurrentUserAndCompletionHandler:^(SPiDResponse *response) {
    if ([response error]) {
        // something went wrong and we need to check what error we received
    } else {
        NSLog(@"The raw response", [response rawJSON]);
    }
}];
{% endhighlight %}

The request returns a `SPiDResponse` object. Before trying to use the message or rawJSON property the client should check for errors using the error property.

There are some wrapper methods as the one above but the you can also write the requests yourself.
For example the following code sends a GET request to /test?user_id=123. Since the SDK takes care of versions the actual path will be ´/api/2/test?user_id=123´
{% highlight objectivec %}
NSString *path = [NSString stringWithFormat:@"/user/%@", @"123"];
[[SPiDClient sharedInstance] apiGetRequestWithPath:path andCompletionHandler:completionHandler];
{% endhighlight %}

And a example to POST ´user_id=123´ to the endpoint ´/test´.
{% highlight objectivec %}
NSString *path = [NSString stringWithFormat:@"/test"];
NSMutableDictionary *data = [NSMutableDictionary dictionary];
[data setObject:@"123" forKey:@"user_id"];
[[SPiDClient sharedInstance] apiPostRequestWithPath:path andBody:data andCompletionHandler:completionHandler];
{% endhighlight %}
For more information see the [API reference](api/index.html "API reference").

