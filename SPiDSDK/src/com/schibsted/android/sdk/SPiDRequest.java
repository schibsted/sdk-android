package com.schibsted.android.sdk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
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
public class SPiDRequest extends AsyncTask<Void, Void, SPiDResponse> {
    private SPiDAsyncCallback callback;

    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> query;
    private Map<String, String> body;

    public SPiDRequest(String method, String url, SPiDAsyncCallback callback) {
        super();
        this.url = url;
        this.method = method;
        this.headers = new HashMap<String, String>();
        this.query = new HashMap<String, String>();
        this.body = new HashMap<String, String>();

        this.callback = callback;

        SPiDLogger.log("Created request: " + url);
    }

    public SPiDRequest(String url, SPiDAsyncCallback callback) {
        this("GET", url, callback);
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

    public boolean isSuccessful(Integer code) {
        return code >= 200 && code < 400;
    }

    @Override
    protected SPiDResponse doInBackground(Void... voids) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getCompleteURL()).openConnection();
            connection.setRequestMethod(this.method);

            // Add headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (method.equals("POST")) { // || method.equals("PUT")) {
                connection.setDoOutput(true);
                OutputStream stream = connection.getOutputStream();
                BufferedWriter writer = null;

                writer = new BufferedWriter(new OutputStreamWriter(stream));
                writer.write(getBodyAsString());
                writer.close();
            } else {
                connection.setDoOutput(false);
            }
            connection.connect();

            // response
            Integer code = connection.getResponseCode();
            InputStream stream = isSuccessful(code) ? connection.getInputStream() : connection.getErrorStream();
            Map<String, String> headers = new HashMap<String, String>();
            for (String key : connection.getHeaderFields().keySet()) {
                headers.put(key, connection.getHeaderFields().get(key).get(0));
            }
            return new SPiDResponse(code, headers, stream);
        } catch (MalformedURLException e) {
            Log.i("SPiD", "MalformedURL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("SPiD", "Error");
            Log.i("SPiD", e.toString());
            Log.i("SPiD", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(SPiDResponse result) {
        super.onPostExecute(result);
        if (result != null) {
            callback.onComplete(result);
        } else {
            callback.onError(new EOFException());
        }
    }

    public void execute() {
        execute((Void) null);
    }
}
