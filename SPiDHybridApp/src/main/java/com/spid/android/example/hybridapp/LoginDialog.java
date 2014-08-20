package com.spid.android.example.hybridapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDUserCredentialTokenRequest;

import java.io.IOException;

public class LoginDialog extends DialogFragment {

    public LoginDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_login, container);

        Button loginButton = (Button) dialogView.findViewById(R.id.dialog_login_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = (EditText) dialogView.findViewById(R.id.dialog_login_edittext_username);
                String email = emailEditText.getText().toString();

                EditText passwordEditText = (EditText) dialogView.findViewById(R.id.dialog_login_edittext_password);
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    SPiDLogger.log("Missing email and/or password");
                    Toast.makeText(getActivity(), "Both Email and Password are required", Toast.LENGTH_LONG).show();
                } else {
                    view.setEnabled(false);
                    SPiDLogger.log("Email: " + email);
                    SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new LoginListener());
                    tokenRequest.execute();
                }
            }
        });

        Button dismissButton = (Button) dialogView.findViewById(R.id.dialog_login_button_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView termsLink = (TextView) dialogView.findViewById(R.id.dialog_login_textview_termslink);
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TermsDialog termsDialog = new TermsDialog();
                termsDialog.show(fragmentManager, "dialog_terms");
            }
        });

        return dialogView;
    }

    private class LoginListener implements SPiDAuthorizationListener {

        private void onError(Exception exception) {
            Button loginButton = (Button) getView().findViewById(R.id.dialog_login_button_login);
            loginButton.setEnabled(true);

            SPiDLogger.log("Error while preforming login: " + exception.getMessage());
            Toast.makeText(getActivity(), "Error while preforming login", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete() {
            MainActivity parent = (MainActivity) getActivity();
            parent.loginWebView();
            dismiss();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            onError(exception);
        }

        @Override
        public void onIOException(IOException exception) {
            onError(exception);
        }

        @Override
        public void onException(Exception exception) {
            onError(exception);
        }
    }
}

