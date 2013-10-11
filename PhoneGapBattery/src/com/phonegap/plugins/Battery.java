package com.phonegap.plugins;

import org.apache.cordova.api.PluginResult;
import org.json.*;

import com.proyecto.PhoneGapBattery.PhoneGapBatteryActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery extends org.apache.cordova.api.Plugin {
    private BatteryReceiver bReceiver;

    public void setContext(PhoneGapBatteryActivity ctx) {
        super.setContext(ctx);
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        bReceiver = new BatteryReceiver();
        ctx.registerReceiver(bReceiver, batteryLevelFilter);
    }

    public PluginResult execute(String action, JSONArray args, String callinglbackId) {
        int level = bReceiver.getLevel();
        try {
            return new PluginResult(PluginResult.Status.OK, "{\"level\":" + level + "}");
        } catch(Exception e) {
            return new PluginResult(PluginResult.Status.INVALID_ACTION, "error: could not read battery!");
        }
    }
}
