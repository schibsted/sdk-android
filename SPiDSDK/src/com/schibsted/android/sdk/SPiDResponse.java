package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.exceptions.SPiDInvalidResponseException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/9/12
 * Time: 8:40 AM
 */
public class SPiDResponse {
    private Integer code;
    private String body;
    private Map<String, String> headers;
    private JSONObject jsonObject;
    private Exception exception;

    public SPiDResponse(IOException e) {
        this.code = -1;
        this.body = "";
        this.headers = new HashMap<String, String>();
        this.exception = e;
    }

    public SPiDResponse(HttpResponse httpResponse) {
        this.code = httpResponse.getStatusLine().getStatusCode();
        this.body = "";
        this.headers = new HashMap<String, String>();
        this.exception = null;
        BufferedReader reader;

        for (Header header : httpResponse.getAllHeaders()) {
            this.headers.put(header.getName(), header.getValue());
        }

        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            for (String line; (line = reader.readLine()) != null; ) {
                this.body += line;
            }
        } catch (IOException exception) {
            this.exception = exception;
        }


        if (this.body.length() > 0) {
            SPiDLogger.log("Received response: " + this.body);
            try {
                this.jsonObject = new JSONObject(this.body);
                if (this.jsonObject.has("error") && !(this.jsonObject.getString("error").equals("null"))) {
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

    public boolean isSuccessful() {
        return getCode() >= 200 && getCode() < 400;
    }

    public int getCode() {
        return code != null ? code : -1;
    }

    public String getBody() {
        return body;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Exception getException() {
        return exception;
    }
}
