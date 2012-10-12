package com.schibsted.android.sdk;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 9:20 PM
 */
public class SPiDRequest {

    private String url;
    private String method;
    private Map<String, String> query;
    private Map<String, String> body;

    public SPiDRequest(String method, String url) {
        this.url = url;
        this.method = method;
        this.query = new HashMap<String, String>();
        this.body = new HashMap<String, String>();
    }

    public SPiDRequest(String url) {
        this("GET", url);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void addQueryParameter(String key, String value) {
        query.put(key, value);
    }

    public void addBodyParameter(String key, String value) {
        body.put(key, value);
    }

    public String getCompleteURL() {
        Log.i("SPiD", url + getQueryAsString());
        return url + getQueryAsString();
    }

    private String encodeURLParameter(String key, String value) {
        return String.format("%s=%s", URLEncoder.encode(key), URLEncoder.encode(value)); // TODO: depricated?
    }

    private String getQueryAsString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : query.entrySet()) {
            if (builder.length() > 0)
                builder.append('&');
            else
                builder.append('?');
            builder.append(encodeURLParameter(entry.getKey(), entry.getValue()));
        }
        return builder.toString();
    }

    private String getBodyAsString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            if (builder.length() > 0)
                builder.append('&');
            builder.append(encodeURLParameter(entry.getKey(), entry.getValue()));
        }
        return builder.toString();
    }

    public SPiDResponse send() {
        // Assert URL
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getCompleteURL()).openConnection();
            connection.setRequestMethod(this.method);
            //connection.setConnectTimeout(60000); // TODO: should not be hardcoded
            //connection.setReadTimeout(600000);

            // Add headers
            /*
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(key, value);
            }
             */

            if (method.equals("POST")) { // || method.equals("PUT")) {
                connection.setDoOutput(true);
                OutputStream stream = connection.getOutputStream();
                BufferedWriter writer = null;

                writer = new BufferedWriter(new OutputStreamWriter(stream));
                writer.write(getBodyAsString());
                writer.close();
                Log.i("SPiD", getBodyAsString());
            }

            return new SPiDResponse(connection);
        } catch (MalformedURLException e) {
            Log.i("SPiD", "MalformedURL");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            Log.i("SPiD", "Error");
            Log.i("SPiD", e.toString());
            Log.i("SPiD", e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
