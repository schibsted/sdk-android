package com.spid.android.example.nativeapp;

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
import com.spid.android.sdk.user.SPiDUser;

import java.util.Map;

public class SignupDialog extends DialogFragment {

    public SignupDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_signup, container);

        Button signupButton = (Button) dialogView.findViewById(R.id.dialog_signup_button_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = (EditText) dialogView.findViewById(R.id.dialog_signup_edittext_username);
                String email = emailEditText.getText().toString();

                EditText passwordEditText = (EditText) dialogView.findViewById(R.id.dialog_signup_edittext_password);
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    SPiDLogger.log("Missing email and/or password");
                    Toast.makeText(getActivity(), "Missing email and/or password", Toast.LENGTH_LONG).show();
                } else {
                    SPiDLogger.log("Email: " + email);
                    SPiDUser.signupWithCredentials(email, password, new SignupListener());
                }
            }
        });

        Button existingUserButton = (Button) dialogView.findViewById(R.id.dialog_signup_button_existinguser);
        existingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(fragmentManager, "dialog_login");
                dismiss();
            }
        });

        TextView termsLink = (TextView) dialogView.findViewById(R.id.dialog_signup_textview_termslink);
        termsLink.setEnabled(true);
        termsLink.setClickable(true);
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

    private class SignupListener implements SPiDAuthorizationListener {

        @Override
        public void onComplete() {
            Toast.makeText(getActivity(), "User created, please check your email for verification", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getFragmentManager();
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.show(fragmentManager, "dialog_login");
            dismiss();
        }

        @Override
        public void onError(Exception exception) {
            if(exception instanceof  SPiDException) {
                final Map<String, String> descriptions = ((SPiDException) exception).getDescriptions();
                if (descriptions.containsKey("blocked")) {
                    String message = descriptions.get("blocked");
                    SPiDLogger.log("Error while performing signup: " + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                } else if (descriptions.containsKey("exists")) {
                    String message = descriptions.get("exists");
                    SPiDLogger.log("Error while performing signup: " + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                } else if (descriptions.containsKey("email")) {
                    String message = descriptions.get("email");
                    SPiDLogger.log("Error while performing signup: " + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                } else if (descriptions.containsKey("password")) {
                    String message = descriptions.get("password");
                    SPiDLogger.log("Error while performing signup: " + message);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                } else {
                    SPiDLogger.log("Error while performing signup: " + exception.getMessage());
                    Toast.makeText(getActivity(), "Error while performing signup", Toast.LENGTH_LONG).show();
                }
            } else {
                SPiDLogger.log("Error while performing signup: " + exception.getMessage());
                Toast.makeText(getActivity(), "Error while performing signup", Toast.LENGTH_LONG).show();
            }
        }
    }
}

