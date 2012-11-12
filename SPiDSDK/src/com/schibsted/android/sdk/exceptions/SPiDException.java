package com.schibsted.android.sdk.exceptions;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Base class for all exceptions in SPiD.
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
    private Integer errorCode;
    private String errorType;

    /**
     * Constructs a new SPiDException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SPiDException(String message) {
        super(message);
        this.error = SPID_EXCEPTION;
        this.errorCode = UNKNOWN_CODE;
        this.errorType = SPID_EXCEPTION;
    }

    /**
     * Constructs a new SPiDException with the specified cause and a detail message of (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause The cause
     */
    public SPiDException(Throwable cause) {
        super(cause);
        this.error = SPID_EXCEPTION;
        this.errorCode = UNKNOWN_CODE;
        this.errorType = SPID_EXCEPTION;
    }

    /**
     * Constructs a new SPiDException with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param throwable The cause
     */
    public SPiDException(String message, Throwable throwable) {
        super(message, throwable);
        this.error = SPID_EXCEPTION;
        this.errorCode = UNKNOWN_CODE;
        this.errorType = SPID_EXCEPTION;
    }

    /**
     * Constructs a new SPiDException with the specified error, description, errorCode and type.
     *
     * @param error       The error as a string, see predefined constants in this class
     * @param description The detail message
     * @param errorCode   The error code
     * @param type        The error type
     */
    public SPiDException(String error, String description, Integer errorCode, String type) {
        super(description);
        this.error = error;
        this.errorCode = errorCode;
        this.errorType = type;
    }

    /*
    public static SPiDException create(String error) {
        return new SPiDException(error, error, UNKNOWN_CODE, SPID_EXCEPTION);
    }
    */

    /**
     * Creates a SPiDException from a JSONObject
     *
     * @param data The JSONObject that contains the error
     * @return The generated exception
     */
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

    /**
     * @return The error as a string, see predefined constants in this class
     */
    public String getError() {
        return error;
    }

    /**
     * @return The error code
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * @return The error type
     */
    public String getErrorType() {
        return errorType;
    }
}
