package com.spid.android.sdk.jwt;

import android.text.TextUtils;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.configuration.TokenType;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.utils.SPiDUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains a JWT (JSON Web token)
 */
public class SPiDJwt {

    private final Date expirationDate;
    private final TokenType tokenType;
    private final SubjectClaim sub;

    private final String issuer;
    private final String audience;
    private final String tokenValue;

    /**
     * Constructs a new SPiDUserAbortedLoginException with the specified detail message.
     *
     * @param issuer            Issuer
     * @param sub               Subject
     * @param audience          Audience
     * @param expirationDate    Expiration time
     * @param tokenType         Token type
     * @param tokenValue        The actual token
     */
    public SPiDJwt(String issuer, SubjectClaim sub, String audience, Date expirationDate, TokenType tokenType, String tokenValue) {
        this.issuer = issuer;
        this.sub = sub;
        this.audience = audience;
        this.expirationDate = expirationDate;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

     private void validate() {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(issuer)) {
            builder.append("JWT is missing value for issuer" + System.getProperty("line.separator"));
        }
        if (sub == null) {
            builder.append("JWT is missing value for sub" + System.getProperty("line.separator"));
        }
        if (TextUtils.isEmpty(audience)) {
            builder.append("JWT is missing value for audience" + System.getProperty("line.separator"));
        }
        if (expirationDate == null) {
            builder.append("JWT is missing value for expirationDate" + System.getProperty("line.separator"));
        }
        if (tokenType == null) {
            builder.append("JWT is missing value for token type" + System.getProperty("line.separator"));
        }
        if (TextUtils.isEmpty(tokenValue)) {
            builder.append("JWT is missing value for token value" + System.getProperty("line.separator"));
        }
        if (SPiDClient.getInstance().getConfig().getSignSecret() == null) {
            builder.append("No signing secret found, cannot use JWT");
        }
        if(!TextUtils.isEmpty(builder.toString())) {
            SPiDLogger.log(builder.toString());
            throw new SPiDException(builder.toString());
        }
    }

    /**
     * Encodes and signs the JWT as a string
     *
     * @return Encoded JWT
     */
    public String encodedJwtString() throws SPiDException {
        validate();

        String headerBase64;
        try {
            JSONObject headerJson = new JSONObject();
            headerJson.put("alg", "HS256");
            headerJson.put("typ", "JWT");
            headerBase64 = SPiDUtils.encodeBase64(headerJson.toString());
        } catch (JSONException e) {
            throw new SPiDException("Error encoding JWT header");
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String date = format.format(expirationDate);

        String claimBase64;
        try {
            JSONObject claimJson = new JSONObject();
            claimJson.put("iss", issuer);
            claimJson.put("sub", sub);
            claimJson.put("aud", audience);
            claimJson.put("exp", date);
            claimJson.put("token_type", tokenType.toString());
            claimJson.put("token_value", tokenValue);
            claimBase64 = SPiDUtils.encodeBase64(claimJson.toString());
        } catch (JSONException e) {
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