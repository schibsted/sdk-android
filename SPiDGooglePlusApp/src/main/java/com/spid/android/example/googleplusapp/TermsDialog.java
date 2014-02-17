package com.spid.android.example.googleplusapp;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.spid.android.example.R;
import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.listener.SPiDRequestListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.reponse.SPiDResponse;
import com.spid.android.sdk.request.SPiDApiGetRequest;
import com.spid.android.sdk.request.SPiDRequest;

import org.json.JSONException;

import java.io.IOException;

/**
 * Contains method for showing a SPiD Terms of use dialog
 */
public class TermsDialog {
    public static void showTerms(final Activity activity) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_terms);

        Button doneButton = (Button) dialog.findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ((MainActivity) activity).showLoadingDialog();

        SPiDRequest termsRequest = new SPiDApiGetRequest("/terms", new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {
                ((MainActivity) activity).dismissLoadingDialog();

                String terms;
                try {
                    terms = result.getJsonObject().getJSONObject("data").getString("terms");
                } catch (JSONException e) {
                    Toast.makeText(activity, "Unable to load terms of use", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuilder termsHtml = new StringBuilder("<html><head><style>\n");
                termsHtml.append("body { text-align: left; color: #666; }\n");
                termsHtml.append("h2 { counter-reset:section; margin: 20px 0 10px 0; font-size: 14px; }\n");
                termsHtml.append("h3 { margin: 15px 0; font-size: 13px; }\n");
                termsHtml.append("h3:before { counter-increment:section; content: counter(section); margin: 0 10px 0 0; }\n");
                termsHtml.append("h4 { margin: 0; text-decoration: underline; font-weight: normal; font-size: 11px; }\n");
                termsHtml.append("p { margin: 0; padding: 0 0 10px 0; font-size: 11px; }\n");
                termsHtml.append("span { font-size: 11px; }\n");
                termsHtml.append("ul { margin: 0px 0 10px 25px; font-size: 11px; list-style: disc outside none; }\n");
                termsHtml.append("li { list-style: disc outside none; }\n");
                termsHtml.append("a:link, a:visited { color: #666; text-decoration: none; }\n");
                termsHtml.append("a:hover {text-decoration: underline;}\n");
                termsHtml.append("</style></head><body>\n");
                termsHtml.append(terms);
                termsHtml.append("</body></html>\n");

                WebView webView = (WebView) dialog.findViewById(R.id.terms_webview);
                webView.loadDataWithBaseURL(null, termsHtml.toString(), "text/html", "utf-8", null);
                dialog.show();
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                ((MainActivity) activity).dismissLoadingDialog();
                SPiDLogger.log("Error loading terms of use: " + exception.getMessage());
                Toast.makeText(activity, "Error loading terms of use", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onIOException(IOException exception) {
                ((MainActivity) activity).dismissLoadingDialog();
                SPiDLogger.log("Error loading terms of use: " + exception.getMessage());
                Toast.makeText(activity, "Error loading terms of use", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                ((MainActivity) activity).dismissLoadingDialog();
                SPiDLogger.log("Error loading terms of use: " + exception.getMessage());
                Toast.makeText(activity, "Error loading terms of use", Toast.LENGTH_LONG).show();
            }
        });
        termsRequest.execute();
    }
}
