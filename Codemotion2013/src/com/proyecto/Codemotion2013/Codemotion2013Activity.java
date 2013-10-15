package com.proyecto.Codemotion2013;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class Codemotion2013Activity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.splash);
        super.loadUrl("file:///android_asset/www/index.html",4000);
    }
}
