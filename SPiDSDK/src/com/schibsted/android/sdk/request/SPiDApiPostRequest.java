package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.SPiDConfiguration;
import com.schibsted.android.sdk.SPiDLogger;
import com.schibsted.android.sdk.SPiDRequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 3/22/13
 * Time: 9:19 AM
 */
public class SPiDApiPostRequest extends SPiDRequest {
    public SPiDApiPostRequest(String url, SPiDRequestListener listener) {
        this(SPiDClient.getInstance().getConfig(), url, listener);
    }

    private SPiDApiPostRequest(SPiDConfiguration config, String url, SPiDRequestListener listener) {
        super(SPiDRequest.POST, config.getServerURL() + "/api/" + config.getApiVersion() + url, listener);
        SPiDLogger.log(config.getServerURL() + "/api/" + config.getApiVersion() + url);
    }
}
