package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/12/12
 * Time: 10:19 AM
 */
public interface SPiDAuthorizationListener {
    public void onComplete();

    void onSPiDException(SPiDException exception);

    void onIOException(IOException exception);

}
