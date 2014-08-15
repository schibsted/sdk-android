package com.spid.android.sdk.exceptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public static final String UNKNOWN_USER = "unknown_user";

    private static final String API_EXCEPTION = "ApiException";
    private static final String OAUTH_EXCEPTION = "OAuthException";
    private static final String SPID_EXCEPTION = "SPiDException";

    private static final Integer UNKNOWN_CODE = -1;

    private String error;
    private Integer errorCode;
    private String errorType;
    private Map<String, String> descriptions;

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
        this.descriptions = new HashMap<String, String>();
        this.descriptions.put("error", message);
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
        this.descriptions = new HashMap<String, String>();
        this.descriptions.put("error", cause.getMessage());
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
        this.descriptions = new HashMap<String, String>();
        this.descriptions.put("error", message);
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
        this.descriptions = new HashMap<String, String>();
        this.descriptions.put("error", description);
    }

    /**
     * Constructs a new SPiDException with the specified error, description, errorCode and type.
     *
     * @param error        The error as a string, see predefined constants in this class
     * @param descriptions The detail messages
     * @param errorCode    The error code
     * @param type         The error type
     */
    public SPiDException(String error, Map<String, String> descriptions, Integer errorCode, String type) {
        super(descriptions.containsKey("error") ? descriptions.get("error") : descriptions.toString());
        this.error = error;
        this.errorCode = errorCode;
        this.errorType = type;
        this.descriptions = descriptions;
    }


    /**
     * Creates a SPiDException from a JSONObject
     *
     * @param data The JSONObject that contains the error
     * @return The generated exception
     */
    public static SPiDException create(JSONObject data) {
        String error;
        String errorCodeString;
        String type;
        Map<String, String> descriptions = new HashMap<String, String>();

        JSONObject errorObject = data.optJSONObject("error");
        if (errorObject != null) {
            error = errorObject.optString("error");
            errorCodeString = errorObject.optString("code");
            type = errorObject.optString("type");

            JSONObject descriptionsJson = errorObject.optJSONObject("description");
            if (descriptionsJson != null) {
                descriptions = descriptionsFromJSONObject(descriptionsJson);
            } else {
                descriptions.put("error", errorObject.optString("description", "Missing error description"));
            }
        } else {
            error = data.optString("error");
            errorCodeString = data.optString("error_code");
            type = data.optString("type");
            descriptions.put("error", data.optString("error_description", "Missing error description"));
        }

        if (error == null && type != null) {
            error = type;
        }

        if (descriptions.isEmpty()) {
            descriptions.put("error", type);
        }

        Integer errorCode;
        try {
//            errorCode = NumberFormat.getInstance().parse(errorCodeString).intValue();
            errorCode = Integer.valueOf(errorCodeString);
        } catch (NumberFormatException e) {
            errorCode = -1;
        }

        type = type != null ? type : SPID_EXCEPTION;

        if (type.equals(API_EXCEPTION)) {
            return new SPiDApiException(error, descriptions, errorCode, type);
        } else if (error != null && (error.equals(INVALID_TOKEN) || error.equals(EXPIRED_TOKEN))) {
            return new SPiDInvalidAccessTokenException(error, descriptions, errorCode, type);
        } else if (error != null && (error.equals(UNKNOWN_USER))) {
            return new SPiDUnknownUserException(error, descriptions, errorCode, type);
        } else if (type.equals(OAUTH_EXCEPTION)) {
            return new SPiDOAuthException(error, descriptions, errorCode, type);
        } else {
            return new SPiDException(error, descriptions, errorCode, SPID_EXCEPTION);
        }
    }

    /**
     * Extracts error description for <code>JSONObject</code>
     *
     * @return Descriptions as a <code>Map</code>
     */
    private static Map<String, String> descriptionsFromJSONObject(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<String, String>();
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, jsonObject.optString(key, "Missing description details"));
        }
        return map;
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

    /**
     * @return Error descriptions
     */
    public Map<String, String> getDescriptions() {
        return descriptions;
    }
}
