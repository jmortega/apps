package com.proyecto.service;

import java.util.ArrayList;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationServiceAux extends Service {
    
    
    
    private static ArrayList <LocationServiceListener> arrayLocationListener = 
                                                new ArrayList<LocationServiceListener>();
    
    private static String TraceName;
    
    private static String LocProvider = LocationManager.NETWORK_PROVIDER;
    
    private static Location currentLocation = null;
    
    public static Location lastKnownLocation = null;
    
    private NotificationManager mNM;
    
    private static LocationManager mLocationManager;
    private RoutePositionListener mLocationListener = null;
    
    // Location Unit (seconds:1 or minutes:60), by default: seconds.
    static Integer LocationUnit = 60;
    
    // Location Periodic in seconds, by default 2 minutes.
    static Integer LocationPeriodic = 2; 
    
    // Minimum distance of gps refresh (in meters)
    static Integer MinimumDistance = 10;
    
    private static ArrayList<Location> points = new ArrayList<Location>();
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
   @Override
    public void onCreate() {
       
       startService();
       
    }

   @Override
   public void onDestroy() {

       if (mLocationListener != null)
           mLocationManager.removeUpdates(mLocationListener);
   }
   
   public static void initRoute()
   {
       points.clear();
   }
   
   public static void setProvider (String provider)
   {
       LocProvider = provider;   
   }
   
   public static Integer registerLocationListener (LocationServiceListener l)
   {
     
       arrayLocationListener.add(l);
      
       return arrayLocationListener.size();
   }
   
   public static void  unRegisterLocationListener (Integer pos)
   {
       arrayLocationListener.remove(pos-1);
   }
   
   public static void setTraceName (String name) 
   {
       TraceName = name;       
   }
   
   public static Location getCurrentLocation ()
   {       
       if (currentLocation == null)
           return  mLocationManager.getLastKnownLocation(LocProvider);
       
       return currentLocation;
   }
   
   //FIXME Change the following method when this service knew user id
   public static void setLastKnownLocation (Location lastKnownLocation)
   {       
       LocationServiceAux.lastKnownLocation = lastKnownLocation;
   }
   
   public static Location getLastKnowLocation ()
   {
       return mLocationManager.getLastKnownLocation(LocProvider);
   }

   /**
    *  This setter method is necessary for manual location cases 
    */
   public static void setCurrentLocation (Location currentLocation){
       LocationServiceAux.currentLocation = currentLocation;
   }
   

    private void startService() {
        
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        mLocationListener = new RoutePositionListener();
        
        Log.e("LGS-Service:",LocProvider);
        
        mLocationManager.requestLocationUpdates( LocProvider, 
                                                 ((LocationPeriodic)* 1000) * LocationUnit, 
                                                 MinimumDistance, 
                                                 mLocationListener);
        
        

        
    }
    
    
    private class RoutePositionListener implements LocationListener 
    {

        public void onLocationChanged(Location loc) {

            if (loc != null) {
                
                // Save the current location
                currentLocation = loc;
 
                if (arrayLocationListener.size() > 0)
                {

                    // Execute all the listener
                    for (int i=0; i<arrayLocationListener.size(); i++)
                        arrayLocationListener.get(i).updateCurrentLocation(loc);                                                    
                    
                }
            }
            

        }


        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }


        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }


        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
    
    public static String getLocProvider()
    {
        return LocProvider;
    }
    

}

