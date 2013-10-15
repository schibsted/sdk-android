package com.schibsted.android.sdk.request;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.configuration.SPiDConfiguration;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;

/**
 * Contains a SPiD Api POST request
 */
public class SPiDApiPostRequest extends SPiDRequest {
    /**
     * Creates a POST API request to SPiD
     *
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    public SPiDApiPostRequest(String path, SPiDRequestListener listener) {
        this(SPiDClient.getInstance().getConfig(), path, listener);
    }

    /**
     * Creates a POST API request to SPiD
     *
     * @param config   Configuration used to get server url and api version
     * @param path     Path for request without api and version, e.g. /user/123
     * @param listener Listener called on completion or failure, can be <code>null</code>
     */
    private SPiDApiPostRequest(SPiDConfiguration config, String path, SPiDRequestListener listener) {
        super(SPiDRequest.POST, config.getServerURL() + "/api/" + config.getApiVersion() + path, listener);
        SPiDLogger.log(config.getServerURL() + "/api/" + config.getApiVersion() + path);
    }
}