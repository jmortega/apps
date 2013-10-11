package com.proyecto.SenchaTouchFavourite;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class SenchaTouchFavouriteActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.splash);
        super.loadUrl("file:///android_asset/www/index.html",4000);
    }
}
