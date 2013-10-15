package com.schibsted.android.sdk.logger;

import android.util.Log;
import com.schibsted.android.sdk.SPiDClient;

/**
 * Helper class used for debug logging
 */
public class SPiDLogger {

    /**
     * Prints to log if debugMode is set
     *
     * @param message Message to log in the Android log
     */
    public static void log(String message) {
        Boolean debug = SPiDClient.getInstance().getDebug();
        if (debug) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

            Log.i(className + "." + methodName + "[Line " + lineNumber + "]:", message);
        }
    }
}
