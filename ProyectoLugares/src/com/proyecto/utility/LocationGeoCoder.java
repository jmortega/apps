package com.proyecto.utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Clase que permite geolocalizar un lugar a partir de un punto obteniendo informacion adicional sobre este punto
 */
public class LocationGeoCoder {

    private static LocationGeoCoder instance  = new LocationGeoCoder();
    private static LocationManager locManager;
    static Context               context;
    ConnectivityManager          connectivityManager;
    NetworkInfo                  wifiInfo, mobileInfo;
    boolean                      connected = false;
    

    public static LocationGeoCoder getInstance(Context ctx,LocationManager locationManager) {
        context = ctx;
        locManager=locationManager;
        return instance;
    }

    /**
     * Obtiene la direccion de un lugar a partir de lantidud y longitud 
     * 
     * @param gp GeoPoint Objeto que contiene latitud y longitud
     * @return String 
     */
    public String obtenerLocationGeoCoder(GeoPoint gp){
        
        boolean red_disponible = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        boolean redOnline=false;
        
        String direccion = "";
        
        if (NetWorkStatus.getInstance(context).isOnline(context)) {
            
            redOnline=true;
        }

        //si hay red y esta Online se intenta obtener la direccion del lugar a partir de latitud y longitud
        
        if(red_disponible && redOnline){
            
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geoCoder.getFromLocation(gp.getLatitudeE6()/ 1E6 , gp.getLongitudeE6()/ 1E6 , 1);
            
                if (addresses!=null && addresses.size() > 0) {
                    for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();i++){

                        direccion += addresses.get(0).getAddressLine(i) + "\n";
                    }
                }
            
            } catch (IOException e) {
                e.printStackTrace();
                return direccion;
            }
        }

        return direccion;
    }

}
