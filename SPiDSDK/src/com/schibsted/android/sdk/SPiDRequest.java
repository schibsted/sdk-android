package com.schibsted.android.sdk;

import android.os.AsyncTask;
import com.schibsted.android.sdk.exceptions.SPiDException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/8/12
 * Time: 9:20 PM
 */
public class SPiDRequest extends AsyncTask<Void, Void, SPiDResponse> {
    private static final Integer MaxRetryCount = 3;

    protected SPiDRequestListener listener;

    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> query;
    private Map<String, String> body;

    private Integer retryCount;
    private Integer maxRetryCount;

    public SPiDRequest(String method, String url, SPiDRequestListener listener) {
        super();
        this.url = url;
        this.method = method;
        this.headers = new HashMap<String, String>();
        this.query = new HashMap<String, String>();
        this.body = new HashMap<String, String>();

        this.listener = listener;

        this.retryCount = 0;
        this.maxRetryCount = MaxRetryCount;

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

    // Used since AsyncTask can only be used once
    public SPiDRequest copy() {
        SPiDRequest request = new SPiDRequest(method, url, listener);
        request.retryCount = retryCount;
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
            HttpRequestBase httpRequest;
            if (this.method.toUpperCase().equals("POST")) {
                httpRequest = new HttpPost(url);

                List<NameValuePair> postList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : body.entrySet()) {
                    postList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }

                ((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(postList));
            } else {
                httpRequest = new HttpGet(url + getQueryAsString());
            }

            List<Header> headerList = new ArrayList<Header>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerList.add(new BasicHeader(entry.getKey(), entry.getValue()));
            }

            Header[] headerArray = new Header[headerList.size()];
            httpRequest.setHeaders(headerList.toArray(headerArray));

            HttpClientParams.setRedirecting(httpRequest.getParams(), false);

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            return new SPiDResponse(httpResponse);
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
                    if (retryCount < maxRetryCount) {
                        SPiDRequest request = this.copy();
                        request.retryCount++;
                        SPiDClient.getInstance().addWaitingRequest(request);
                        SPiDClient.getInstance().refreshAccessToken(null);
                        SPiDLogger.log("Retrying attempt: " + request.retryCount + " for request: " + request.url);
                    }
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

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
}
