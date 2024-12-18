package com.adijith.mywebapp.service;

import com.adijith.mywebapp.model.BusStop;
import com.adijith.mywebapp.model.PassengerLocation;
import com.adijith.mywebapp.model.HeatMapLocation;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class HeatMapService {

    private static final double EARTH_RADIUS = 6371.0;  // Radius in kilometers
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Value("${firebase.passenger-location-path}")
    private String passengerLocationPath;

    @Value("${firebase.bus-stop-path}")
    private String busStopPath;

    // Calculate the distance between two points using the Haversine formula
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;  // in km
    }

    // Fetch passenger locations from Firebase asynchronously
    public void fetchPassengerLocations(Consumer<List<PassengerLocation>> callback) {
        DatabaseReference passengerRef = firebaseDatabase.getReference(passengerLocationPath);
        passengerRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PassengerLocation> passengerLocations = new ArrayList<>();
                Map<String, Object> passengerData = (Map<String, Object>) task.getResult().getValue();
                if (passengerData != null) {
                    for (Map.Entry<String, Object> entry : passengerData.entrySet()) {
                        // Parse the data into the PassengerLocation object
                        PassengerLocation passengerLocation = parsePassengerData(entry.getValue());
                        passengerLocations.add(passengerLocation);
                    }
                }
                callback.accept(passengerLocations);  // Invoke the callback with the passenger data
            } else {
                task.getException().printStackTrace();  // Log any errors that occurred
            }
        });
    }

    // Fetch bus stops from Firebase asynchronously
    public void fetchBusStops(Consumer<List<BusStop>> callback) {
        DatabaseReference busStopRef = firebaseDatabase.getReference(busStopPath);
        busStopRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<BusStop> busStops = new ArrayList<>();
                Map<String, Object> busStopData = (Map<String, Object>) task.getResult().getValue();
                if (busStopData != null) {
                    for (Map.Entry<String, Object> entry : busStopData.entrySet()) {
                        // Parse the data into the BusStop object
                        BusStop busStop = parseBusStopData(entry.getValue());
                        busStops.add(busStop);
                    }
                }
                callback.accept(busStops);  // Invoke the callback with the bus stop data
            } else {
                task.getException().printStackTrace();  // Log any errors that occurred
            }
        });
    }

    // Parse passenger data from Firebase into PassengerLocation object
    private PassengerLocation parsePassengerData(Object data) {
        // Implement logic for parsing passenger data into PassengerLocation object
        return new PassengerLocation();  // Placeholder
    }

    // Parse bus stop data from Firebase into BusStop object
    private BusStop parseBusStopData(Object data) {
        // Implement logic for parsing bus stop data into BusStop object
        return new BusStop();  // Placeholder
    }

    // Get heat map data based on passenger locations and bus stops
    public void getHeatMapData(Consumer<List<HeatMapLocation>> callback) {
        // Create a CountDownLatch to wait for both fetch operations to complete
        final CountDownLatch latch = new CountDownLatch(2);
        final List<PassengerLocation> passengerLocations = new ArrayList<>();
        final List<BusStop> busStops = new ArrayList<>();

        // Fetch passenger locations asynchronously
        fetchPassengerLocations(passengerLocationsList -> {
            passengerLocations.addAll(passengerLocationsList);
            latch.countDown();  // Decrement the latch after passenger locations are fetched
        });

        // Fetch bus stops asynchronously
        fetchBusStops(busStopsList -> {
            busStops.addAll(busStopsList);
            latch.countDown();  // Decrement the latch after bus stops are fetched
        });

        // Wait for both fetch operations to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // After both fetch operations are complete, compute the heatmap
        Map<String, Integer> locationFrequency = new HashMap<>();
        for (PassengerLocation passenger : passengerLocations) {
            for (BusStop busStop : busStops) {
                double distanceToBoarding = calculateDistance(
                        passenger.getBoarding().getLatitude(),
                        passenger.getBoarding().getLongitude(),
                        busStop.getLatitude(),
                        busStop.getLongitude()
                );

                if (distanceToBoarding <= busStop.getRadius() / 1000.0) {
                    String key = busStop.getName() + ":" + passenger.getBoarding().getLatitude() + ":" + passenger.getBoarding().getLongitude();
                    locationFrequency.put(key, locationFrequency.getOrDefault(key, 0) + 1);
                }

                double distanceToGettingOff = calculateDistance(
                        passenger.getGettingOff().getLatitude(),
                        passenger.getGettingOff().getLongitude(),
                        busStop.getLatitude(),
                        busStop.getLongitude()
                );

                if (distanceToGettingOff <= busStop.getRadius() / 1000.0) {
                    String key = busStop.getName() + ":" + passenger.getGettingOff().getLatitude() + ":" + passenger.getGettingOff().getLongitude();
                    locationFrequency.put(key, locationFrequency.getOrDefault(key, 0) + 1);
                }
            }
        }

        // Create heatmap locations from frequency data
        List<HeatMapLocation> heatMapLocations = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : locationFrequency.entrySet()) {
            String[] parts = entry.getKey().split(":");
            String busStopName = parts[0];
            double latitude = Double.parseDouble(parts[1]);
            double longitude = Double.parseDouble(parts[2]);
            int count = entry.getValue();

            HeatMapLocation heatMapLocation = new HeatMapLocation(latitude, longitude, busStopName, count);
            heatMapLocations.add(heatMapLocation);
        }

        // Return the computed heatmap data via the callback
        callback.accept(heatMapLocations);
    }
}
