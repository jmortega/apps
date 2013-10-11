package com.proyecto.SenchaTouchFeedBurner;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class SenchaTouchFeedBurnerActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.splash);
        super.loadUrl("file:///android_asset/www/index.html",3000);
    }
}
