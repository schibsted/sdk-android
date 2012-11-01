package com.schibsted.android.sdk.exceptions;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/31/12
 * Time: 11:52 AM
 */
public class SPiDException extends RuntimeException {
    public static final String REDIRECT_URI_MISMATCH = "redirect_uri_mismatch";
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String ACCESS_DENIED = "access_denied";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String INVALID_GRANT = "invalid_grant";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_CLIENT_ID = "invalid_client_id"; // Replaced by "invalid_client" in draft 10 of oauth 2.0
    public static final String INVALID_CLIENT_CREDENTIALS = "invalid_client_credentials"; // Replaced by "invalid_client" in draft 10 of oauth 2.0
    public static final String INVALID_TOKEN = "invalid_token";
    public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
    public static final String EXPIRED_TOKEN = "expired_token";

    private static final String API_EXCEPTION = "ApiException";
    private static final String OAUTH_EXCEPTION = "OAuthException";
    private static final String SPID_EXCEPTION = "SPiDException";

    private static final Integer UNKNOWN_CODE = -1;

    private String error;
    private String errorCode;
    private String errorType;

    public SPiDException(String msg) {
        super(msg);
    }

    public SPiDException(Throwable throwable) {
        super(throwable);
    }

    public SPiDException(String msg, Throwable t) {
        super(msg, t);
    }

    public SPiDException(String error, String description, Integer errorCode, String type) {
        super(description);


        //To change body of created methods use File | Settings | File Templates.
    }

    public static SPiDException create(String error) {
        // TODO: Add better response
        return new SPiDException(error, error, UNKNOWN_CODE, SPID_EXCEPTION);
    }

    public static SPiDException create(JSONObject data) {
        String error;
        String description;
        String errorCodeString;
        String type;

        JSONObject errorObject = data.optJSONObject("error");
        if (errorObject != null) {
            error = errorObject.optString("error");
            description = errorObject.optString("description");
            errorCodeString = errorObject.optString("code");
            type = errorObject.optString("type");
        } else {
            error = data.optString("error");
            description = data.optString("error_description");
            errorCodeString = data.optString("error_code");
            type = data.optString("type");
        }

        if (error == null && type != null) {
            error = type;
        }

        if (description == null && type != null) {
            description = type;
        }

        Integer errorCode;
        try {
            errorCode = NumberFormat.getInstance().parse(errorCodeString).intValue();
        } catch (ParseException e) {
            errorCode = -1;
        }

        type = type != null ? type : SPID_EXCEPTION;

        if (type.equals(API_EXCEPTION)) {
            return new SPiDApiException(error, description, errorCode, type);
        } else if (type.equals(OAUTH_EXCEPTION)) {
            return new SPiDOAuthException(error, description, errorCode, type);
        } else {
            return new SPiDException(error, description, errorCode, SPID_EXCEPTION);
        }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
