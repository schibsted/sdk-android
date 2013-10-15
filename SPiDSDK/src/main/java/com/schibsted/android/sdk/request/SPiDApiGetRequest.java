package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.listener.SPiDRequestListener;

/**
 * Contains a SPiD Api GET request
 */
public class SPiDApiGetRequest extends SPiDRequest {
    /**
     * Creates a GET API request to SPiD
     *
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public SPiDApiGetRequest(String path, SPiDRequestListener listener) {
        this(SPiDClient.getInstance().getConfig(), path, listener);
    }

    /**
     * Creates a GET API request to SPiD
     *
     * @param config   Configuration used to get server url and api version
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    private SPiDApiGetRequest(SPiDConfiguration config, String path, SPiDRequestListener listener) {
        super(SPiDRequest.GET, config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
    }
}
