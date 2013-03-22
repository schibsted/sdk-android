package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.SPiDConfiguration;
import com.schibsted.android.sdk.SPiDRequestListener;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 3/22/13
 * Time: 9:20 AM
 */
public class SPiDApiGetRequest extends SPiDRequest {
    public SPiDApiGetRequest(String url, SPiDRequestListener listener) {
        this(SPiDClient.getInstance().getConfig(), url, listener);
    }

    private SPiDApiGetRequest(SPiDConfiguration config, String url, SPiDRequestListener listener) {
        super(SPiDRequest.GET, config.getServerURL() + "/api/" + config.getApiVersion() + url, listener);
    }
}
