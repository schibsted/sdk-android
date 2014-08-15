package com.spid.android.sdk.jwt;

import android.text.TextUtils;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.utils.SPiDUtils;

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

    private String issuer;
    private String sub;
    private String audience;
    private Date expirationDate;
    private String tokenType;
    private String tokenValue;

    /**
     * Constructs a new SPiDUserAbortedLoginException with the specified detail message.
     *
     * @param issuer            Issuer
     * @param sub
     * @param audience          Audience
     * @param expirationDate    Expiration time
     * @param tokenType         Token type(currently only facebook)
     * @param tokenValue        The actual token
     */
    public SPiDJwt(String issuer, String sub, String audience, Date expirationDate, String tokenType, String tokenValue) {
        this.issuer = issuer;
        this.sub = sub;
        this.audience = audience;
        this.expirationDate = expirationDate;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    /**
     * Validates the JWT
     */
    private boolean validate() {
        boolean isNoError = true;
        if (TextUtils.isEmpty(issuer)) {
            SPiDLogger.log("JWT is missing value for issuer");
            isNoError = false;
        }
        if (TextUtils.isEmpty(sub)) {
            SPiDLogger.log("JWT is missing value for sub");
            isNoError = false;
        }
        if (TextUtils.isEmpty(audience)) {
            SPiDLogger.log("JWT is missing value for audience");
            isNoError = false;
        }
        if (expirationDate == null) {
            SPiDLogger.log("JWT is missing value for expirationDate");
            isNoError = false;
        }
        if (TextUtils.isEmpty(tokenType)) {
            SPiDLogger.log("JWT is missing value for token type");
            isNoError = false;
        }
        if (TextUtils.isEmpty(tokenValue)) {
            SPiDLogger.log("JWT is missing value for token value");
            isNoError = false;
        }
        return isNoError;
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
        String date = format.format(expirationDate);

        String claimBase64;
        try {
            JSONObject claimJson = new JSONObject();
            claimJson.put("issuer", issuer);
            claimJson.put("sub", sub);
            claimJson.put("audience", audience);
            claimJson.put("expirationDate", date);
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