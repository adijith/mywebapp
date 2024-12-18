package com.adijith.mywebapp.firebase;

import com.adijith.mywebapp.model.Place;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseService {

    private final DatabaseReference busStopsRef;
    private final DatabaseReference placesRef;

    public FirebaseService() {
        // Initialize Firebase database references
        busStopsRef = FirebaseDatabase.getInstance().getReference("busStops");
        placesRef = FirebaseDatabase.getInstance().getReference("places");
    }

    // Process and add places within a radius to the respective bus stop as buspoints
    public void addPlacesWithinRadiusToBusStops(double centerLat, double centerLon, double radiusInKm, String busStopId) {
        System.out.println("\n--- Processing Places for Bus Stop: " + busStopId + " ---");
        System.out.println("Center: (" + centerLat + ", " + centerLon + ") | Radius: " + radiusInKm + " km");

        // Read data from 'places'
        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    System.out.println("No data found in 'places' node.");
                    return;
                }

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    if (place != null) {
                        double distance = calculateDistance(centerLat, centerLon, place.getLatitude(), place.getLongitude());
                        System.out.println("Place: " + place.getName() + " | Distance: " + distance + " km");

                        if (distance <= radiusInKm) {
                            System.out.println("Place '" + place.getName() + "' is within the radius. Adding to bus stop.");
                            addBusPointToBusStop(busStopId, place);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error reading from Firebase: " + databaseError.getMessage());
            }
        });
    }

    // Add a single place as a buspoint under the given bus stop
    private void addBusPointToBusStop(String busStopId, Place place) {
        DatabaseReference busStopRef = busStopsRef.child(busStopId).child("buspoints");

        // Check if the place already exists as a buspoint
        busStopRef.orderByChild("name").equalTo(place.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("Place '" + place.getName() + "' already exists as a buspoint. Skipping.");
                    return;
                }

                // Generate a new buspoint ID
                String busPointId = busStopRef.push().getKey();
                if (busPointId != null) {
                    Map<String, Object> busPointDetails = new HashMap<>();
                    busPointDetails.put("id", busPointId);
                    busPointDetails.put("name", place.getName());
                    busPointDetails.put("latitude", place.getLatitude());
                    busPointDetails.put("longitude", place.getLongitude());

                    // Add the buspoint to the bus stop
                    busStopRef.child(busPointId).setValue(busPointDetails, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            System.out.println("Error adding buspoint: " + databaseError.getMessage());
                        } else {
                            System.out.println("Buspoint '" + place.getName() + "' added successfully to bus stop " + busStopId);
                        }
                    });
                } else {
                    System.out.println("Failed to generate a buspoint ID for '" + place.getName() + "'.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error checking existing buspoints: " + databaseError.getMessage());
            }
        });
    }

    // Calculate distance between two geographic coordinates (Haversine formula)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
