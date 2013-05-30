package com.proyecto.service;

import java.text.DecimalFormat;

import android.location.Location;

public class LocationUtilsAux {
    public static float distance;
    
    public static final double MAX_LOG_DISTANCE_SHOWN = 3.5;
    
    /**
     * Movements definitions
     */
    public static final int MOVE_LEFT = -90;
    public static final int MOVE_RIGHT = 90;
    public static final int MOVE_STRAIGHT = 0;
    public static final int MOVE_BACK = 180;
    
    /**
     * Device distance in meters
     */
    public static final float DEV_DISTANCE = 0.3f;
    
    /**
     * The average tall (in meters) of a human being
     * There is an approximated maximum error of +/- 0.25 meters
     */
//  public static final float MEAN_TALL = 1.75f;
    //public static final float FIXED_DISTANCE = 1;
    
    public static final int AUTO        = 0;
    public static final int KILOMETERS  = 1;
    public static final int METERS      = 2;
    
    /**
     * Earth's equatorial radius
     */
    private static final float a = 6378137; 
    
    /**
     * Earth's polar radius
     */
    private static final float b = 6356752.3f; 
    
    /**
     * The equivalent in meters for a distance of one latitude second
     */
    private static final float LATITUDE_DEGREE = 30.82f;
    
    private static float absolute_height = 1.75f;
    
    public static void setUserHeight(float absolute_height){
        LocationUtilsAux.absolute_height = absolute_height;
    }
    
    /**
     * Calculate the distance (in meters) between two Location points
     * 
     * @param Source Location Point
     * @param Destination Location Point
     * 
     * @return The distance in meters.
     */
    
    public static float calculateDistance (Location pointSource, Location pointDest)
    {
        
        if ((pointSource == null) || (pointDest== null))
            return 0;
        
        float dist = pointSource.distanceTo(pointDest);
        
        return dist;
    }
    
    /**
     * Calculate the distance (in meters) between two points
     * 
     * @param Source (Latitude, Longitude) Point
     * @param Destination (Latitude, Longitude) Location Point
     * 
     * @return The distance in meters.
     */
    
    public static float calculateDistance (float[] pointSource, float[] pointDest)
    {
        Location point1 = new Location("Tag");
        Location point2 = new Location("Tag");
        
        point1.setLatitude(pointSource[0]);
        point1.setLongitude(pointSource[1]);
        
        point2.setLatitude(pointDest[0]);
        point2.setLongitude(pointDest[1]);
        
        float dist = point1.distanceTo(point2);
        
        return dist;
    }
    
    
    /**
     * Calculate the distance (in meters) between two Location points
     * 
     * @param The distance in meters
     * @param The kind of conversion (AUTO,KILOMETERS,METERS)
     * 
     * @return The String with the information of distance
     */
    
    public static String displayDistance (float dist, int unit)
    {
        
        String distString = null;
        
        switch (unit)
        {
            case AUTO:
                
                if (dist >= 1000.0)
                    distString = distanceToKilometers(dist);
                else
                    distString = distanceToMeters(dist);
                
                break;
            case KILOMETERS:
                
                distString = distanceToKilometers(dist);
                break;
                
            case METERS:
                
                distString = distanceToMeters(dist);
                break;
            
        }
        
        return distString;
        
        
    }
    
    private static String distanceToKilometers (float dist)
    {
        dist = (float) (dist / 1000.0);
        DecimalFormat df = new DecimalFormat("0.00");
        
        return String.valueOf(df.format(dist)) + " km.";
    }
    
    private static String distanceToMeters (float dist)
    {
        return String.valueOf(Math.round(dist)) + " m.";
    }
    
    public static double distanceLog(double dist){
        double log = 1;
        
        if(dist == 0)
            return 0;
        log = Math.max(1, Math.min(MAX_LOG_DISTANCE_SHOWN, Math.log10(dist)))/MAX_LOG_DISTANCE_SHOWN;
        
        return log;
    }
    
    /**
     * This function estimates the roll the user has to use in order to see the resource
     * @param distance is the distance to the resource
     * @param ob_height is the height of the object
     * @return This returns the roll in degrees
     */
    public static float calculateRoll (float distance, float ob_height){
        float roll = 0, h;
        
        h = absolute_height - ob_height;
        
        roll = (float) Math.toDegrees(Math.atan2(distance, h));
        
        return roll;
    }
    
    /**
     * This function calculates the azimuth between an user and a resource
     * @param user_roll is the
     * @param res_roll
     * @return
     */
    public static float calculateRollFromUser(float user_roll, float res_roll){
        float roll;
        
        roll = user_roll - res_roll;
        
        return roll;
    }
    
    /**
     * This function calculates the estimated height of a resource from the floor
     * @param roll is the angle that the user and the Z axe defines
     * @param distance is the distance between the user and the resource
     * @return This returns the estimated height of the resource
     */
    public static float calculateResourceHeight(float roll, float distance){
        float h_relative, h_real=0;
        
        h_relative = (float) (distance / Math.tan(Math.toRadians(roll)));
        
        h_real = Math.max(0, absolute_height - h_relative);
        
        return h_real;
    }
    
    /**
     * This function calculates the azimuth of a resource from the user's position
     * @param source is the user's (latitude, longitude)
     * @param resource is the resource's (latitude, longitude)
     * 
     * @return This returns the azimuth in degrees
     */
    public static float calculateResourceAzimuth(float[] source, float[] resource){
        float azimuth, dist_lat, dist_lng;
        
        float lat_degree = LATITUDE_DEGREE * 3600;
        float long_degree = (float) ((Math.PI/180)*Math.cos(Math.toRadians(source[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(source[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(source[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(source[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(source[0])),2))
                        ));
        
        dist_lat = (resource[0] - source[0]) * lat_degree;
        dist_lng = (resource[1] - source[1]) * long_degree;
        
        azimuth = (float) Math.toDegrees(Math.atan2(dist_lng, dist_lat));
        
        if(azimuth<0)
            azimuth += 360;
        
        return azimuth;
    }
    
    public static float calculateResourceLocAzimuth(Location user, Location object){
        float azimuth, dist_lat, dist_lng;

        float lat_degree = LATITUDE_DEGREE * 3600;
        float long_degree = (float) ((Math.PI/180)*Math.cos(Math.toRadians(user.getLatitude())) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(user.getLatitude())),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(user.getLatitude())),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(user.getLatitude())),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(user.getLatitude())),2))
                ));

        dist_lat = (float) ((object.getLatitude() - user.getLatitude()) * lat_degree);
        dist_lng = (float) ((object.getLongitude() - user.getLongitude()) * long_degree);

        azimuth = (float) Math.toDegrees(Math.atan2(dist_lng, dist_lat));

        if(azimuth<0)
            azimuth += 360;

        return azimuth;
    }
    
    
    /**
     * This function calculates the relative azimuth of the resource from the user
     * @param user_azimuth is the azimuth of the user
     * @param resource_azimuth is the azimuth of the resource
     * @return The returned value is the angle in degrees
     */
    public static float calculateAzimuthFromUser(float user_azimuth, float resource_azimuth){
        float azimuth = 500, azimuth_Aux;
        
        if(Math.abs(azimuth_Aux =  resource_azimuth - user_azimuth) < Math.abs(azimuth))
            azimuth = azimuth_Aux;
        
        if(Math.abs(azimuth_Aux =  (resource_azimuth - 360) - user_azimuth) < Math.abs(azimuth))
            azimuth = azimuth_Aux;
        
        if(Math.abs(azimuth_Aux =  (360 + resource_azimuth) - user_azimuth) < Math.abs(azimuth))
            azimuth = azimuth_Aux;
        
        
        return azimuth;
    }
    
    /**
     * This function calculates the position of the resource you want to fix.
     *  @param p0 Contains the (latitude, longitude) tuple of the first point
     *  @param p1 Contains the (latitude, longitude) tuple of the second point
     *  @param or0 This is the angle of the first point that the orientation sensor give us
     *  @param or1 This is the angle of the second point that the orientation sensor give us
     *  
     *  @return Returns the (latitude, longitude) tuple of the resource point
     */
    public static float[] calculateIntersection(float[] p0_deg, float[] p1_deg, float or0, float or1){
        float m0, m1, p_res[];
        float lat_degree, long_degree;

        lat_degree = LATITUDE_DEGREE * 3600;
        long_degree =(float) ((Math.PI/180)*Math.cos(Math.toRadians(p0_deg[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))
                        ));
        
        m0 = (float) (Math.tan(Math.toRadians(90 - or0)));
        m1 = (float) (Math.tan(Math.toRadians(90 - or1)));
        
        //Cartesian system
        float[] p0 = {0, 0};
        float[] p1 = { long_degree * (p1_deg[1] - p0_deg[1]), 
                lat_degree * (p1_deg[0] - p0_deg[0]) };
        
        
        p_res = new float[2];
        p_res[1] = (m0 * p0[0] - m1 * p1[0] + p1[1] - p0[1]) / (m0 - m1);
        p_res[0] = (m0 * (p_res[1] - p0[0]) + p0[1]) / lat_degree + p0_deg[0];
        
        p_res[1] = p_res[1] / long_degree + p0_deg[1];
        
        return p_res;
    }
    
    public static float[] calculateIntersection(float[] p0, float or0, float distance){
        return distance2Coord(p0, or0, distance);
    }
    
    public static float[] calculateIntersectionElevation(float[] p0_deg, float[] p1_deg, float or, float el0, float el1){
        float m0, m1, p_res[];
        float lat_degree, long_degree;
        
        lat_degree = LATITUDE_DEGREE * 3600;
        long_degree =(float) ((Math.PI/180)*Math.cos(Math.toRadians(p0_deg[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))
                        ));
        
        m0 = (float) (Math.tan(Math.toRadians(el0 - 90)));
        m1 = (float) (Math.tan(Math.toRadians(el1 - 90)));
        
        float y = lat_degree * (p1_deg[0] - p0_deg[0]);
        float x = long_degree * (p1_deg[1] - p0_deg[1]);
        
        distance = (float) Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2) );
        
        float difference = (float) (Math.toDegrees(Math.atan2(y, x)));
        if (difference > 90 )
            difference -= 360;
        
        if ( ( Math.abs(difference - (90 - or)) > 90 ) &&
                ( Math.abs(difference - (90 - or)) < 270 ) ) {
            distance = -distance;
        }
        
        //Cartesian system
        float[] p0 = {0, 0};
        float[] p1 = { distance, 0 };
        
        distance = (m0 * p0[0] - m1 * p1[0] + p1[1] - p0[1]) / (m0 - m1);
        
        p_res = distance2Coord(p0_deg, or, distance);
        
        return p_res;
    }
    
    /**
     * This function calculates the angle of an object regarding to the camera.
     * @param p0_deg User's position (lat, long)
     * @param p1_deg Object position (lat, long)
     * @param or0 User's actual orientation
     * @param or1 Object orientation regarding to the user
     * @param camDistance Distance between camera and user
     * @return The angle required or null if there's an error
     */
    public static float calculateAngleToCam(float[] p0_deg, float[] p1_deg, float or0, float or1, float camDistance){
        float alpha;
        float lat_degree, long_degree;

        lat_degree = LATITUDE_DEGREE * 3600;
        long_degree =(float) ((Math.PI/180)*Math.cos(Math.toRadians(p0_deg[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))
                        ));
        
        //Cartesian system
        float[] p0 = {0, 0};
        float[] pCam = {(float) (camDistance * Math.sin(Math.toRadians(or0))), (float) (camDistance * Math.cos(Math.toRadians(or0)))};
        float[] p1 = { long_degree * (p1_deg[1] - p0_deg[1]), 
                lat_degree * (p1_deg[0] - p0_deg[0]) };
        
        float[] vector1 = { pCam[0] - p0[0], pCam[1] - p0[1] };
        float[] vector2 = { p1[0] - pCam[0], p1[1] - pCam[1] };
        
        float mod1 = (float) Math.sqrt(Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2));
        float mod2 = (float) Math.sqrt(Math.pow(vector2[0], 2) + Math.pow(vector2[1], 2));;
        alpha = (float) Math.toDegrees(Math.acos((vector1[0] * vector2[0] + vector1[1] * vector2[1])/(mod1 * mod2)));
        
        if(or1 < 0)
            alpha = -alpha;
        
        return alpha;
    }
    
    /**
     * This function calculates the intersection point given two angles previously measured.
     * @param p0_deg User's position (lat,long)
     * @param or User's actual orientation
     * @param theta Angle measure between two points.
     * @param alpha Object angle regarding the camera
     * @param camDistance The distance of the camera
     * @return The point given in (lat,long) units
     */
    public static float[] calculateAngleIntersection(float[] p0_deg, float or0, float or1, float theta_s, float alpha, float inclination, float camDistance){
        float m0, m1, p_res[], p_int[], p_rot[];
        float lat_degree, long_degree;
        
        float theta = theta_s * (1 - Math.abs((inclination-90)/90));
        if(theta>=alpha)
            // 10 meters is the maximum of this method
            return calculateIntersection(p0_deg, or0, 10);
        
        lat_degree = LATITUDE_DEGREE * 3600;
        long_degree =(float) ((Math.PI/180)*Math.cos(Math.toRadians(p0_deg[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(p0_deg[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(p0_deg[0])),2))
                        ));
        
        m0 = (float) (Math.tan(Math.toRadians(theta)));
        m1 = (float) (Math.tan(Math.toRadians(alpha)));
        
        //Cartesian system
        float[] p0 = {0, 0};
        float[] p1 = { camDistance, 0 };
        
        p_int = new float[2];
        p_int[0] = (m0 * p0[0] - m1 * p1[0] + p1[1] - p0[1]) / (m0 - m1);
        p_int[1] = (m0 * (p_int[0] - p0[0]) + p0[1]);
        
        p_rot = new float[2];
        p_rot = turnPoint(p_int, or1 - 90);
        
        p_res = new float[2];
        p_res[0] = p_rot[1] /lat_degree + p0_deg[0];
        p_res[1] = p_rot[0] /long_degree + p0_deg[1];
        
        return p_res;
    }
    
    /**
     * This function estimates, given a distance, where is the resource. It suppose that the user is directly pointing to it.
     * @param p0 is the point where the user is
     * @param or is the azimuth of the user
     * @param d is the distance to the resource
     * 
     * @return the coordinates of the resource
     */
    public static float[] distance2Coord(float[] p0, float or, float d){
        float lat_degree, long_degree, lat, lng;
        float[] res = new float[2];
        
        lat_degree = LATITUDE_DEGREE * 3600;
        long_degree =(float) ((Math.PI/180)*Math.cos(Math.toRadians(p0[0])) *
                Math.sqrt((Math.pow(a, 4)*Math.pow(Math.cos(Math.toRadians(p0[0])),2) +
                        Math.pow(b, 4)*Math.pow(Math.sin(Math.toRadians(p0[0])),2))/
                        (Math.pow(a, 2)*Math.pow(Math.cos(Math.toRadians(p0[0])),2) +
                                Math.pow(b, 2)*Math.pow(Math.sin(Math.toRadians(p0[0])),2))
                        ));
        
        lat = (float) (d*Math.cos(Math.toRadians(or)))/lat_degree;
        lng = (float) (d*Math.sin(Math.toRadians(or)))/long_degree;
        
        res[0] = lat + p0[0];
        res[1] = lng + p0[1];
        
        return res;
    }
    
    public static Location moveTo(Location loc, float orientation, int rotation, int distance){
        Location loc_converted = new Location("");
        float[] coords = {(float) loc.getLatitude(), (float) loc.getLongitude()};
        
        coords = LocationUtilsAux.calculateIntersection(coords, 
                LocationUtilsAux.turnAngle(orientation, rotation), distance);
        
        loc_converted.setLatitude(coords[0]);
        loc_converted.setLongitude(coords[1]);
        
        return loc_converted;
        
    }
    
    public static float[] moveTo(float[] loc, float orientation, int rotation, int distance){
        float[] coords;
        
        coords = LocationUtilsAux.calculateIntersection(loc, 
                LocationUtilsAux.turnAngle(orientation, rotation), distance);
        
        return coords;
        
    }
    
    public static float turnAngle(float orientation, float angle){
        float new_angle;
        
        new_angle = orientation + angle;
        if(new_angle >= 360)
            new_angle -= 360;
        
        if(new_angle < 0)
            new_angle += 360;
        
        return new_angle;
    }
    
    public static float[] turnPoint(float[] point, float angle ){
        float[] p_res = {0, 0};
        
        p_res[0] = (float) (point[0] * Math.cos(Math.toRadians(angle)) + point[1] * Math.sin(Math.toRadians(angle)));
        p_res[1] = (float) (point[1] * Math.cos(Math.toRadians(angle)) - point[0] * Math.sin(Math.toRadians(angle)));
        
        return p_res;
    }
    
}
