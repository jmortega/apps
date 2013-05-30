package com.proyecto.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.proyecto.activity.R;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Clase que actua como listener cuando va a obtener la posicion GPS
 */
public class LugaresLocationListener implements LocationListener {

    private Context       context;
    private MapController mapController;

    /**
     * Constructor por parametros
     * 
     * @param context
     *            Context Contexto de la aplicacion
     * @param mapController
     *            MapController Controlador del mapa
     */
    public LugaresLocationListener(Context context, MapController mapController) {
        this.context = context;
        this.mapController = mapController;
    }

    /**
     * Método que se ejecuta cuando detecta que ha habido un cambio en la
     * localización
     * 
     * @param localtion
     *            Location Objeto localizacion
     */
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {

            int latitud = (int) (location.getLatitude() * 1E6);
            int longitud = (int) (location.getLongitude() * 1E6);
            GeoPoint gp = new GeoPoint(latitud, longitud);

            if(gp!=null){

                mapController.setCenter(gp);
                
                mapController.animateTo(gp);

                /*Toast.makeText(context, context.getText(R.string.posicion_actual) + "\n" + gp.getLatitudeE6() / 1E6 + "," + gp.getLongitudeE6() / 1E6,
                    Toast.LENGTH_SHORT).show();*/
            }
        }

    }

    @Override
    public void onProviderDisabled(String arg0) {
        Toast.makeText(context, context.getText(R.string.gps_deshabilitado), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(String arg0) {
        Toast.makeText(context, context.getText(R.string.gps_habilitado), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Toast.makeText(context, context.getText(R.string.status_changed), Toast.LENGTH_LONG).show();
    }

}
