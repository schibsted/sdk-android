package com.schibsted.android.sdk;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/12/12
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */

public interface SPiDAsyncTaskCompleteListener<T> {
    public void onComplete(T result);

    public void onError(T result);
}
