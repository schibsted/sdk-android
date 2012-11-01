package com.schibsted.android.sdk;

import android.os.AsyncTask;
import com.schibsted.android.sdk.exceptions.SPiDException;

import java.io.*;
import java.net.HttpURLConnection;
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
    protected SPiDRequestListener listener;

    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> query;
    private Map<String, String> body;

    public SPiDRequest(String method, String url, SPiDRequestListener listener) {
        super();
        this.url = url;
        this.method = method;
        this.headers = new HashMap<String, String>();
        this.query = new HashMap<String, String>();
        this.body = new HashMap<String, String>();

        this.listener = listener;

        SPiDLogger.log("Created request: " + url);
    }

    public SPiDRequest(String url, SPiDRequestListener listener) {
        this("GET", url, listener);
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

    public String getCompleteURL() throws UnsupportedEncodingException {
        return url + getQueryAsString();
    }

    private String encodeURLParameter(String key, String value) throws UnsupportedEncodingException {
        return String.format("%s=%s", URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8")); // TODO: depricated?
    }

    private String getQueryAsString() throws UnsupportedEncodingException {
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

    private String getBodyAsString() throws UnsupportedEncodingException {
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

    // Used since AsyncTask can only be used once
    public SPiDRequest copy() {
        SPiDRequest request = new SPiDRequest(method, url, listener);
        request.setHeaders(headers);
        request.setQuery(query);
        request.setBody(body);
        return request;
    }

    private void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    private void setQuery(Map<String, String> query) {
        this.query = query;
    }

    private void setBody(Map<String, String> body) {
        this.body = body;
    }

    @Override
    protected SPiDResponse doInBackground(Void... voids) {
        try {
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) new URL(getCompleteURL()).openConnection();
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
            HttpURLConnection.setFollowRedirects(false);
            Integer code = connection.getResponseCode();
            InputStream stream = isSuccessful(code) ? connection.getInputStream() : connection.getErrorStream();
            Map<String, String> headers = new HashMap<String, String>();
            for (String key : connection.getHeaderFields().keySet()) {
                headers.put(key, connection.getHeaderFields().get(key).get(0));
            }
            return new SPiDResponse(code, headers, stream);
        } catch (IOException e) {
            return new SPiDResponse(e);
        }
    }

    @Override
    protected void onPostExecute(SPiDResponse response) {
        super.onPostExecute(response);
        doOnPostExecute(response);
    }

    protected void doOnPostExecute(SPiDResponse response) {
        Exception exception = response.getException();
        if (exception != null) {
            if (exception instanceof IOException) {
                listener.onIOException((IOException) exception);
            } else if (exception instanceof SPiDException) {
                String error = ((SPiDException) exception).getError();
                if (error != null && (error.equals(SPiDException.EXPIRED_TOKEN) || error.equals(SPiDException.INVALID_TOKEN))) {
                    SPiDClient.getInstance().addWaitingRequest(this.copy());
                    SPiDClient.getInstance().refreshAccessToken(null);
                } else {
                    listener.onSPiDException((SPiDException) exception);
                }
            } else {
                SPiDLogger.log("Received unknown exception: " + exception.getMessage());
            }
        } else {
            listener.onComplete(response);
        }
    }

    public void execute() {
        execute((Void) null);
    }
}
