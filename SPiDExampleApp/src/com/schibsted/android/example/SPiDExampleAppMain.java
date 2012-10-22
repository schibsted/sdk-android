package com.schibsted.android.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.schibsted.android.sdk.SPiDAsyncCallback;
import com.schibsted.android.sdk.SPiDClient;
import com.schibsted.android.sdk.SPiDLogger;
import com.schibsted.android.sdk.SPiDResponse;

/**
 * Created with IntelliJ IDEA.
 * User: mikaellindstrom
 * Date: 10/5/12
 * Time: 1:39 PM
 */

public class SPiDExampleAppMain extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    protected class RefreshTokenButtonListener implements View.OnClickListener {
        Context context;

        public RefreshTokenButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().refreshAccessToken(new SPiDAsyncCallback() {
                @Override
                public void onComplete(SPiDResponse result) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error while refreshing access token");
                    Toast.makeText(context, "Error while refreshing access token", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected class OneTimeCodeButtonListener implements View.OnClickListener {
        Context context;

        public OneTimeCodeButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().refreshAccessToken(new SPiDAsyncCallback() {
                @Override
                public void onComplete(SPiDResponse result) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onError(Exception exception) {
                    SPiDLogger.log("Error getting one time code");
                    Toast.makeText(context, "Error getting one time code", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected class LogoutButtonListener implements View.OnClickListener {
        Context context;

        public LogoutButtonListener(Context context) {
            this.context = context;
        }

        public void onClick(View v) {
            SPiDClient.getInstance().logoutSPiDAPI(new SPiDAsyncCallback() {
                @Override
                public void onComplete(SPiDResponse result) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onError(Exception exception) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }
    }
}
