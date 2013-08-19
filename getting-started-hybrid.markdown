---
title: Getting started with hybrid login
layout: default
---
Hybrid login
============

Hybrid apps are applications with utilizes a WebView within the app. They require two logins, one for the native part of the application and one for the webview. SPiD has full support for this and the setup is similar to the native login exept we add steps to catch logins from the WebView. It is available on SPiD version 2.8.8 or later.

The following flow diagram gives a overview of how it works.

![](images/hybridflow.png)

First the native and/or facebook needs to be setup. After that we need to add the server client id and redirect uri to the SPiD configuration.
```java
SPiDConfiguration config = new SPiDConfigurationBuilder()
                .clientID("your-client-id")
                .clientSecret("your-client-secret")
                .appURLScheme("your-app-url-scheme")
                .serverURL("your-spidserver-url")
                .signSecret("your-secret-sign-key")
                .serverClientID("your-server-client_id")
                .serverRedirectUri("your-server-client-redirect-uri")
                .context(this)
                .build();
```

When the app is successfully logged in nativly we need to login to the WebView. This is done by adding a step after login is completed in the login listener for the token request.
```java
SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new SPiDAuthorizationListener() {
    @Override 
    public void onComplete() {
    	loginWebView();
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
```

Then we add a new method to execute the login in the WebView.
```java
private void loginWebView() {
	SPiDClient.getInstance().getSessionCode(new SPiDRequestListener() {
        @Override
        public void onComplete(SPiDResponse result) {
            try {
                String code = result.getJsonObject().getJSONObject("data").getString("code");

                // The session login endpoint is SPiDUrl/session/code, e.i. https://payment.schibsted.no/session/code
                String url = SPiDClient.getInstance().getConfig().getServerURL() + "/session/" + code;
                // load the url in the WebView, when done it will redirect to the server client redirect uri 
                webView.loadUrl(url);
            } catch (JSONException exception) {
                // JSON exception should not happen since this would be a incorrect JSON response from SPiD
            }

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
```

We also need to setup a WebViewClient to override with url:s should be loaded.

```xml
webView.setWebViewClient(new MainWebViewClient() {
	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String serverUrl = SPiDClient.getInstance().getConfig().getServerURL();
            // SPiD login and logout pages should be intercepted
            String serverLoginUrl = serverUrl + "/auth/login";
            String serverLogoutUrl = serverUrl + "/logout";
            // serverRedirectUri is where the WebView is redirected on login completion
            String serverRedirectUri = "your-server-redirect-uri";

            if (url.startsWith(serverLoginUrl)) {
                // Open native login instead of opening the page
                return true;
            } else if (url.startsWith(serverLogoutUrl)) {
                // Should logout through API instead of opening the page
                return true;
            } else if (url.startsWith(serverAccountSummaryUrl)) {
                // User successfully logged in and received a SPiD cookie in the WebView.
            }

            return false;
        }
});
```

Now any login in the WebView will result in a native login window and the user will be logged in to both. Further more, if there is a token available at app start the login can be made directly in the background to prevent login when the user selects restricted content. For more information look at the example in the SDK.




