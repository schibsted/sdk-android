package com.schibsted.android.sdk;

import com.schibsted.android.sdk.exceptions.SPiDException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        this.exception = e;
    }

    public SPiDResponse(Integer code, Map<String, String> headers, InputStream inputStream) {
        this.code = code;
        this.headers = headers;
        this.exception = null;
        BufferedReader reader = null;

        this.body = "";
        reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            for (String line; (line = reader.readLine()) != null; ) {
                this.body += line;
            }
            if (this.body.length() > 0) {
                this.jsonObject = new JSONObject(this.body);
            } else {
                this.jsonObject = new JSONObject();
            }
            if (this.jsonObject.has("error") && !(this.jsonObject.getString("error").equals("null"))) {
                this.exception = SPiDException.create(this.jsonObject);
            }
        } catch (IOException exception) {
            this.exception = exception;
        } catch (JSONException exception) {
            this.jsonObject = new JSONObject();
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

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
