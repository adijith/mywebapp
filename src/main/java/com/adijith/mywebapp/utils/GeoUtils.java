package com.adijith.mywebapp.utils;

public class GeoUtils {

    // Check if a place is within the radius of a bus stop
    public static boolean isWithinRadius(double busStopLat, double busStopLng, double placeLat, double placeLng, int radius) {
        double earthRadius = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(placeLat - busStopLat);
        double dLng = Math.toRadians(placeLng - busStopLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(busStopLat)) * Math.cos(Math.toRadians(placeLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c; // Distance in km
        return distance <= radius / 1000.0; // Convert radius from meters to km
    }
}
