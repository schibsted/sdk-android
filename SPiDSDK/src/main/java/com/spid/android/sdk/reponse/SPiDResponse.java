package com.spid.android.sdk.reponse;

import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.exceptions.SPiDInvalidResponseException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains a response from SPiD
 */
public class SPiDResponse {

    private final Integer code;
    private final Map<String, String> headers;

    private String body;
    private JSONObject jsonObject;
    private Exception exception;

    /**
     * Constructor for SPiDResponse
     *
     * @param exception exception
     */
    public SPiDResponse(Exception exception) {
        this.code = SPiDException.UNKNOWN_CODE;
        this.body = "";
        this.headers = new HashMap<String, String>();
        this.exception = exception;
    }

    /**
     * Constructor for SPiDResponse
     *
     * @param httpResponse The response from SPiD
     */
    public SPiDResponse(HttpResponse httpResponse) {
        this.code = httpResponse.getStatusLine().getStatusCode();
        this.headers = new HashMap<String, String>();
        this.exception = null;
        BufferedReader reader = null;

        for (Header header : httpResponse.getAllHeaders()) {
            this.headers.put(header.getName(), header.getValue());
        }

        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            body = builder.toString();
        } catch (IOException exception) {
            this.exception = exception;
        } finally {
            closeQuietly(reader);
        }

        if (!this.body.isEmpty()) {
            SPiDLogger.log("Received response: " + this.body);
            try {
                this.jsonObject = new JSONObject(this.body);
                if (this.jsonObject.has("error") && !("null".equals(this.jsonObject.getString("error")))) {
                    this.exception = SPiDException.create(this.jsonObject);
                }
            } catch (JSONException e) {
                this.jsonObject = new JSONObject();
                this.exception = new SPiDInvalidResponseException("Invalid response from SPiD: " + body);
            }
        } else {
            this.jsonObject = new JSONObject();
        }

        if (!isSuccessful()) {
            this.exception = SPiDException.create(this.jsonObject);
        }
    }

    private void closeQuietly(BufferedReader reader) {
        if(reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * @return If request was successful, i.e. 200 >= http code < 400
     */
    public boolean isSuccessful() {
        return getCode() >= HttpURLConnection.HTTP_OK && getCode() < HttpURLConnection.HTTP_BAD_REQUEST;
    }

    /**
     * @return The http status code
     */
    public int getCode() {
        return code != null ? code : SPiDException.UNKNOWN_CODE;
    }

    /**
     * @return The http body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return The http body as a <code>JSONObject</code>
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * @return Exception if there was any otherwise <code>null</code>
     */
    public Exception getException() {
        return exception;
    }
}
