package com.project.SearchImageSenchaTouch;

import org.apache.cordova.DroidGap;


import android.os.Bundle;

public class SearchImageSenchaTouchActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.screen);
        super.loadUrl("file:///android_asset/www/index.html",4000);
    }
}
