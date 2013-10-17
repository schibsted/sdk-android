package com.spid.android.example.nativeapp;

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

import com.spid.android.sdk.exceptions.SPiDException;
import com.spid.android.sdk.listener.SPiDAuthorizationListener;
import com.spid.android.sdk.logger.SPiDLogger;
import com.spid.android.sdk.user.SPiDUser;

import java.io.IOException;

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
        final Toast toast = Toast.makeText(getActivity(), "Missing email and/or password", Toast.LENGTH_LONG);

        Button signupButton = (Button) dialogView.findViewById(R.id.dialog_signup_button_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = (EditText) dialogView.findViewById(R.id.dialog_signup_edittext_username);
                String email = emailEditText.getText().toString();

                EditText passwordEditText = (EditText) dialogView.findViewById(R.id.dialog_signup_edittext_password);
                String password = passwordEditText.getText().toString();

                if (email.equals("") || password.equals("")) {
                    SPiDLogger.log("Missing email and/or password");
                    if (!toast.getView().isShown()) {
                        toast.show();
                    }
                } else {
                    SPiDLogger.log("Email: " + email + " password: " + password);
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

        private void onError(Exception exception) {
            SPiDLogger.log("Error while preforming signup: " + exception.getMessage());
            Toast.makeText(getActivity(), "Error while preforming signup", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete() {
            Toast.makeText(getActivity(), "User created, please check your email for verification", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getFragmentManager();
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.show(fragmentManager, "dialog_login");
            dismiss();
        }

        @Override
        public void onSPiDException(SPiDException exception) {
            if (exception.getDescriptions().containsKey("blocked")) {
                String message = exception.getDescriptions().get("blocked");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("exists")) {
                String message = exception.getDescriptions().get("exists");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("email")) {
                String message = exception.getDescriptions().get("email");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else if (exception.getDescriptions().containsKey("password")) {
                String message = exception.getDescriptions().get("password");
                SPiDLogger.log("Error while preforming signup: " + message);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                onError(exception);
            }
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

