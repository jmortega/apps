package com.proyecto.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Clase que permite obtener el estado de la conexion
 */
public class NetWorkStatus {

    private static NetWorkStatus instance  = new NetWorkStatus();
    static Context               context;
    ConnectivityManager          connectivityManager;
    NetworkInfo                  wifiInfo, mobileInfo;
    boolean                      connected = false;

    public static NetWorkStatus getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    public boolean isOnline(Context con) {
        try {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

}
