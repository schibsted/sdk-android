package com.spid.android.example.googleplusapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.spid.android.example.R;

/**
 * Contains the login activity
 */
public class GooglePlusLoginDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_googleplus_login, container);
        SignInButton signInButton = (SignInButton) dialogView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.connectToGooglePlus();
                dismiss();
            }
        });

        TextView termsLink = (TextView) dialogView.findViewById(R.id.terms_link);
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsDialog.showTerms(getActivity());
            }
        });

        return dialogView;
    }
}
