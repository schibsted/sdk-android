package com.spid.android.example.googleplusapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.spid.android.example.R;

/**
 * Contains the login activity
 */
public class NoAccountDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_no_account, container);
        final MainActivity mainActivity = (MainActivity) getActivity();

        Button createNewUser = (Button) dialogView.findViewById(R.id.createNewUser);
        createNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.createUserFromGooglePlus();
                dismiss();
            }
        });

        Button attachGoogleUser = (Button) dialogView.findViewById(R.id.attachGoogleUser);
        attachGoogleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                NativeLoginDialog nativeLoginDialog = new NativeLoginDialog();
                nativeLoginDialog.show(fragmentManager, "dialog_native_login");
                dismiss();
            }
        });

        Button cancelDialog = (Button) dialogView.findViewById(R.id.cancelDialog);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.logoutFromGooglePlus();
                mainActivity.displayLoginScreen(true);
                dismiss();
            }
        });

        return dialogView;
    }
}
