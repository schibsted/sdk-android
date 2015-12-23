SPiD SDK for Android [![Build Status](https://travis-ci.org/schibsted/sdk-android.svg?branch=master)](https://travis-ci.org/schibsted/sdk-android)
================

Supported Android version is 2.3 (API level 9) or greater. The SDK is currently using buildtools version 23.0.2

The SDK can be built using the gradle wrapper included by running the following command(assuming that the android-sdk is installed):
```
./gradlew -p SPiDSDK build
```

The compiled library will be located in SPiDSDK/build/libs/

The SDK can also be installed to your local maven repository with the following command:
```
./gradlew -p SPiDSDK uploadArchives
```

If you would like to use external repository you have to modify your application build.gradle file

```
repositories {
    maven { url 'https://raw.github.com/schibsted/sdk-android/master/SPiDSDK/repo/' }
}

dependencies {
    compile 'no.schibstedpayment:SPiD-Android:1.3.10@aar'
}
```

From version 1.3.10 a local broadcast is sent out from the SDK every time the access token changes. If you're interested to receive updates when a user logs in our out the easiest way is to create a BroadcastReceiver to listen for these events.

        BroadcastReceiver messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String userId = intent.getStringExtra(SPiDAccessToken.USER_ID);
                Log.d(TAG, "User id was changed, new value: " + userId);
            }
        };
        
Register to listen for events:

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(SPiDAccessToken.SPID_ACCESS_TOKEN_EVENT));
                
Unregister:

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

For information and the development guides see our [Documentation](http://schibsted.github.com/sdk-android "Documentation").


