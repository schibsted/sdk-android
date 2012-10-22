package com.schibsted.android.sdk;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/15/12
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SPiDLogger {

    public final static boolean DEBUG = true;

    public static void log(String message) {
        if (DEBUG) {
            // SPiDDebugLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__);
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

            Log.i(className + "." + methodName + "[Line " + lineNumber + "]:", message);
        }
    }
}
