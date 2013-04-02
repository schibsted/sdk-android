package com.schibsted.android.sdk.jwt;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.utils.SPiDUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class SPiDJwt {

    private String iss;
    private String sub;
    private String aud;
    private Date exp;
    private String tokenType;
    private String tokenValue;

    public SPiDJwt(String iss, String sub, String aud, Date exp, String tokenType, String tokenValue) {
        this.iss = iss;
        this.sub = sub;
        this.aud = aud;
        this.exp = exp;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public SPiDJwt(Map<String, Object> map) {

    }

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

    public String encodedJwtString() throws Exception {
        if (!validate()) {
            throw new Exception("Invalid JWT");
        }
        if (SPiDClient.getInstance().getConfig().getSignSecret() == null) {
            SPiDLogger.log("No signing secret found, cannot use JWT");
            throw new Exception("Missing sign secret");
        }

        JSONObject headerJson = new JSONObject();
        headerJson.put("alg", "HS256");
        headerJson.put("typ", "JWT");

        String header = headerJson.toString();
        String headerBase64;
        try {
            headerBase64 = SPiDUtils.encodeBase64(header);
        } catch (UnsupportedEncodingException e1) {
            throw new Exception("Error encoding JWT header");
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String date = format.format(exp);

        JSONObject claimJson = new JSONObject();
        claimJson.put("iss", iss);
        claimJson.put("sub", sub);
        claimJson.put("aud", aud);
        claimJson.put("exp", date);
        claimJson.put("token_type", tokenType);
        claimJson.put("token_value", tokenValue);
        String claim = claimJson.toString();

        String claimBase64;
        try {
            claimBase64 = SPiDUtils.encodeBase64(claim);
        } catch (UnsupportedEncodingException e1) {
            throw new Exception("Error encoding JWT claim");
        }

        String signSecret = SPiDClient.getInstance().getConfig().getSignSecret();
        String payload = headerBase64 + "." + claimBase64;
        String signature = SPiDUtils.getHmacSHA256(signSecret, payload);
        return payload + "." + signature;

    }
}