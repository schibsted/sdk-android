SPiD SDK for Android [![Build Status](https://travis-ci.org/schibsted/sdk-android.svg?branch=master)](https://travis-ci.org/schibsted/sdk-android)
================

Supported Android version is 2.3 (API level 9) or greater. The SDK is currently using buildtools version 21.1.2.

The SDK can be built using the gradle wrapper included by running the following command(assuming that the android-sdk is installed):
```
./gradlew -p SPiDSDK build
```

The compiled library will be located in SPiDSDK/build/libs/

The SDK can also be installed to your local maven repository with the following command:
```
./gradlew -p SPiDSDK install
```

For information and the development guides see our [Documentation](http://schibsted.github.com/sdk-android "Documentation").
