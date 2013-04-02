package com.schibsted.android.sdk;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import java.io.File;

public class SPiDTestRunner extends RobolectricTestRunner {
    public SPiDTestRunner(Class testClass) throws InitializationError {
        // defaults to "AndroidManifest.xml", "res" in the current directory
        super(testClass, new File("SPiDSDKTests"));
    }
}
