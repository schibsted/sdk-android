package com.schibsted.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

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
        Log.i("SPiD", "STARTING INTENT");
        setContentView(R.layout.main);
    }
}
