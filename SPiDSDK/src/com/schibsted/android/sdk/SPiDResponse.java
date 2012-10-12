package com.schibsted.android.sdk;

import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/9/12
 * Time: 8:40 AM
 */
public class SPiDResponse {
    private int code;
    private String body;
    private InputStream stream;
    private Map<String, String> headers;
    private JSONObject jsonObject;

    SPiDResponse(HttpURLConnection connection) throws IOException {
        try {
            //connection.setDoOutput(false);
            Log.i("SPiD", "Trying to connect");
            connection.connect();
            Log.i("SPiD", "Connected");
            code = connection.getResponseCode();
            Log.i("SPiD", String.format("code: %d", code));
            stream = isSuccessful() ? connection.getInputStream() : connection.getErrorStream();
            headers = new HashMap<String, String>();
            for (String key : connection.getHeaderFields().keySet()) {
                headers.put(key, connection.getHeaderFields().get(key).get(0));
            }
            BufferedReader reader = null;
            try {
                String result = "";
                reader = new BufferedReader(new InputStreamReader(stream));
                for (String line; (line = reader.readLine()) != null; ) {
                    result += line;
                }
                jsonObject = new JSONObject(result);

            } catch (UnknownHostException error) {
                throw error;
            }
        } catch (Exception e) {
            Log.i("SPiD", e.toString());
            Log.i("SPiD", e.getMessage());
        } finally {
            connection.disconnect();
        }
    }

    public boolean isSuccessful() {
        return getCode() >= 200 && getCode() < 400;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
