package com.spid.android.example.googleplusapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.spid.android.example.R;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.request.SPiDUserCredentialTokenRequest;

/**
 * Contains the login activity
 */
public class NativeLoginDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_native_login, container);

        Button loginButton = (Button) dialogView.findViewById(R.id.dialog_login_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = (EditText) getView().findViewById(R.id.dialog_login_edittext_username);
                EditText passwordEditText = (EditText) getView().findViewById(R.id.dialog_login_edittext_password);
                doNativeLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.dialog_login_back_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                NoAccountDialog noAccountDialog = new NoAccountDialog();
                noAccountDialog.show(fragmentManager, "dialog_no_account");
                dismiss();
            }
        });

        TextView termsLink = (TextView) dialogView.findViewById(R.id.dialog_login_textview_termslink);
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsDialog.showTerms(getActivity());
            }
        });

        return dialogView;
    }

    private void doNativeLogin(String email, final String password) {
        if (email.equals("") || password.equals("")) {
            SPiDLogger.log("Missing email and/or password");
            Toast.makeText(getActivity(), "Missing email and/or password", Toast.LENGTH_LONG).show();
        } else {
            SPiDLogger.log("Email: " + email + " password: " + password);
            final MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showLoadingDialog();
            SPiDUserCredentialTokenRequest tokenRequest = new SPiDUserCredentialTokenRequest(email, password, new SPiDAuthorizationListener() {

                @Override
                public void onComplete() {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.attachGooglePlusUser();
                    dismiss();
                }

                @Override
                public void onError(Exception exception) {
                    mainActivity.dismissLoadingDialog();
                    SPiDLogger.log("Received error: " + exception.getMessage());
                    Toast.makeText(getActivity(), "Received error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            tokenRequest.execute();
        }

    }
}
