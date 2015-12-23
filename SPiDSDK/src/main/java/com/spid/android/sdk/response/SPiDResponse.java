package com.spid.android.sdk.response;

import android.text.TextUtils;

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
        this.headers = new HashMap<>();
        this.exception = exception;
    }

    /**
     * Constructor for SPiDResponse
     *
     * @param httpResponse The response from SPiD
     */
    public SPiDResponse(HttpResponse httpResponse) {
        code = httpResponse.getStatusLine().getStatusCode();
        headers = new HashMap<>();
        exception = null;
        BufferedReader reader = null;

        for (Header header : httpResponse.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
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
        } catch (IOException ioe) {
            exception = ioe;
        } finally {
            closeQuietly(reader);
        }

        if (!TextUtils.isEmpty(body)) {
            try {
                this.jsonObject = new JSONObject(this.body);
                if (jsonObject.has("error") && !("null".equals(jsonObject.getString("error")))) {
                    exception = SPiDException.create(jsonObject);
                }
            } catch (JSONException e) {
                jsonObject = new JSONObject();
                exception = new SPiDInvalidResponseException("Invalid response from SPiD: " + body);
            }
        } else {
            jsonObject = new JSONObject();
        }

        if (!isSuccessful()) {
            exception = SPiDException.create(jsonObject);
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
     * @return If request was successful, i.e. http code between 200 and 400
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
