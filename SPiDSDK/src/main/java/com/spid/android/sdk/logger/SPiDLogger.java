package com.spid.android.sdk.logger;

import android.util.Log;

import com.spid.android.sdk.SPiDClient;

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
        log(message, null);
    }

    /**
     * Prints to log if debugMode is set including an exception
     *
     * @param message Message to log in the Android log
     * @param exception The exception to log
     */
    public static void log(String message, Exception exception) {
        Boolean debug = SPiDClient.getInstance().isDebug();
        if (debug) {
            int calleeStackIndex = exception == null ? 4 : 3;
            String fullClassName = Thread.currentThread().getStackTrace()[calleeStackIndex].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[calleeStackIndex].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[calleeStackIndex].getLineNumber();

            if(exception != null) {
                Log.i(className + "." + methodName + "[Line " + lineNumber + "]:", message, exception);
            } else {
                Log.i(className + "." + methodName + "[Line " + lineNumber + "]:", message);
            }
        }
    }
}
