package com.proyecto.BrujulaMobile;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class BrujulaActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.screen);
        super.loadUrl("file:///android_asset/www/index.html",3000);
    }
}
