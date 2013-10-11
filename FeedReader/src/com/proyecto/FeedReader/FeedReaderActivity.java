package com.proyecto.FeedReader;

import org.apache.cordova.DroidGap;


import android.os.Bundle;

public class FeedReaderActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.splash);
        super.loadUrl("file:///android_asset/www/index.html",3000);
    }
}
