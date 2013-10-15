package com.schibsted.android.example.hybridapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.exceptions.SPiDException;
import com.schibsted.android.sdk.listener.SPiDRequestListener;
import com.schibsted.android.sdk.logger.SPiDLogger;
import com.schibsted.android.sdk.reponse.SPiDResponse;
import com.schibsted.android.sdk.request.SPiDApiGetRequest;
import com.schibsted.android.sdk.request.SPiDRequest;

import org.json.JSONException;

import java.io.IOException;

/**
 * Contains method for showing a SPiD Terms of use dialog
 */
public class TermsDialog extends DialogFragment {

    public TermsDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_terms, container);

        Button dismissButton = (Button) view.findViewById(R.id.dialog_terms_button_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TermsDialog.this.dismiss();
            }
        });

        this.getTermsOfUse();

        return view;
    }

    private void getTermsOfUse() {
        String termsQuery = "/terms?client_id" + SPiDClient.getInstance().getConfig().getClientID();
        SPiDRequest termsRequest = new SPiDApiGetRequest(termsQuery, new SPiDRequestListener() {
            @Override
            public void onComplete(SPiDResponse result) {

                String terms;
                try {
                    terms = result.getJsonObject().getJSONObject("data").getString("terms");
                } catch (JSONException exception) {
                    SPiDLogger.log("Error decoding terms of use response from SPiD: " + exception.getMessage());
                    Toast.makeText(getActivity(), "Unable to load terms of use", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuilder termsHtml = new StringBuilder("<html><head><style>\n");
                termsHtml.append("body { text-align: left; color: #666; font-family: Helvetica, Arial, sans-serif; font-size: 13px; }\n");
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

                Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);

                ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.dialog_terms_progressbar);
                progressBar.setAnimation(animFadeOut);
                progressBar.setVisibility(View.GONE);

                WebView webView = (WebView) getView().findViewById(R.id.dialog_terms_webview);
                webView.loadDataWithBaseURL(null, termsHtml.toString(), "text/html", "utf-8", null);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSPiDException(SPiDException exception) {
                SPiDLogger.log("Error loading dialog_terms of use: " + exception.getMessage());
                Toast.makeText(getActivity(), "Error loading dialog_terms of use", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onIOException(IOException exception) {
                SPiDLogger.log("Error loading dialog_terms of use: " + exception.getMessage());
                Toast.makeText(getActivity(), "Error loading dialog_terms of use", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                SPiDLogger.log("Error loading dialog_terms of use: " + exception.getMessage());
                Toast.makeText(getActivity(), "Error loading dialog_terms of use", Toast.LENGTH_LONG).show();
            }
        });
        termsRequest.execute();
    }
}
