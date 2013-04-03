package com.schibsted.android.sdk.jwt;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.utils.SPiDUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains a JWT (JSON Web token)
 */
public class SPiDJwt {

    private String iss;
    private String sub;
    private String aud;
    private Date exp;
    private String tokenType;
    private String tokenValue;

    /**
     * Constructs a new SPiDUserAbortedLoginException with the specified detail message.
     *
     * @param iss        Issuer
     * @param aud        Audience
     * @param exp        Expiration time
     * @param tokenType  Token type(currently only facebook)
     * @param tokenValue The actual token
     */
    public SPiDJwt(String iss, String sub, String aud, Date exp, String tokenType, String tokenValue) {
        this.iss = iss;
        this.sub = sub;
        this.aud = aud;
        this.exp = exp;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    /**
     * Validates the JWT
     */
    private boolean validate() {
        if (iss == null || iss.length() <= 0) {
            SPiDLogger.log("JWT is missing value for iss");
            return false;
        }
        if (sub == null || sub.length() <= 0) {
            SPiDLogger.log("JWT is missing value for sub");
            return false;
        }
        if (aud == null || aud.length() <= 0) {
            SPiDLogger.log("JWT is missing value for aud");
            return false;
        }
        if (exp == null) {
            SPiDLogger.log("JWT is missing value for exp");
            return false;
        }
        if (tokenType == null || tokenType.length() <= 0) {
            SPiDLogger.log("JWT is missing value for token type");
            return false;
        }
        if (tokenValue == null || tokenValue.length() <= 0) {
            SPiDLogger.log("JWT is missing value for token value");
            return false;
        }
        return true;
    }

    /**
     * Encodes and signs the JWT as a string
     *
     * @return Encoded JWT
     */
    public String encodedJwtString() throws SPiDException {
        if (!validate()) {
            throw new SPiDException("Invalid JWT");
        }
        if (SPiDClient.getInstance().getConfig().getSignSecret() == null) {
            SPiDLogger.log("No signing secret found, cannot use JWT");
            throw new SPiDException("Missing sign secret");
        }

        String headerBase64;
        try {
            JSONObject headerJson = new JSONObject();
            headerJson.put("alg", "HS256");
            headerJson.put("typ", "JWT");
            headerBase64 = SPiDUtils.encodeBase64(headerJson.toString());
        } catch (UnsupportedEncodingException e1) {
            throw new SPiDException("Error encoding JWT header");
        } catch (JSONException e2) {
            throw new SPiDException("Error encoding JWT header");
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String date = format.format(exp);

        String claimBase64;
        try {
            JSONObject claimJson = new JSONObject();
            claimJson.put("iss", iss);
            claimJson.put("sub", sub);
            claimJson.put("aud", aud);
            claimJson.put("exp", date);
            claimJson.put("token_type", tokenType);
            claimJson.put("token_value", tokenValue);
            claimBase64 = SPiDUtils.encodeBase64(claimJson.toString());
        } catch (UnsupportedEncodingException e1) {
            throw new SPiDException("Error encoding JWT claim");
        } catch (JSONException e2) {
            throw new SPiDException("Error encoding JWT header");
        }

        String signSecret = SPiDClient.getInstance().getConfig().getSignSecret();
        String payload = headerBase64 + "." + claimBase64;
        String signature;
        try {
            signature = SPiDUtils.getHmacSHA256(signSecret, payload);
        } catch (Exception e) {
            throw new SPiDException("Error generating signature for JWT token");
        }
        return payload + "." + signature;

    }
}