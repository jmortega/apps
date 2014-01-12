package com.proyecto.PhoneGapBattery;

import org.apache.cordova.DroidGap;

import android.os.Bundle;

public class PhoneGapBatteryActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.phonegap);
        super.loadUrl("file:///android_asset/www/index.html",4000);
    }
}
