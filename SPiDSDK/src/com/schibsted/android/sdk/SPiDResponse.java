package com.schibsted.android.sdk;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
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

    public SPiDResponse(Integer code, Map<String, String> headers, InputStream inputStream) throws IOException {
        try {
            this.headers = headers;
            BufferedReader reader = null;
            try {
                body = "";
                reader = new BufferedReader(new InputStreamReader(inputStream));
                for (String line; (line = reader.readLine()) != null; ) {
                    body += line;
                }
                jsonObject = new JSONObject(body);
            } catch (UnknownHostException error) {
                throw error;
            }
        } catch (Exception e) {
            SPiDLogger.log("Exception");
            SPiDLogger.log(e.toString());
            SPiDLogger.log(e.getMessage());
        } finally {
            //connection.disconnect();
        }
    }

    public SPiDResponse() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isSuccessful() {
        return getCode() >= 200 && getCode() < 400;
    }

    public int getCode() {
        return code;
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
}
