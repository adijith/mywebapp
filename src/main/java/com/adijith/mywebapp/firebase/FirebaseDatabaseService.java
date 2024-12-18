package com.adijith.mywebapp.firebase;

import com.adijith.mywebapp.model.BusStop;
import com.adijith.mywebapp.model.BusPoint;
import com.adijith.mywebapp.model.Place;
import com.adijith.mywebapp.utils.GeoUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class FirebaseDatabaseService {

    private static final Logger logger = Logger.getLogger(FirebaseDatabaseService.class.getName());

    // Fetching bus stops from Firebase
    public CompletableFuture<List<BusStop>> fetchBusStops() {
        CompletableFuture<List<BusStop>> future = new CompletableFuture<>();
        List<BusStop> busStopsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("busStops");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("Firebase Data Snapshot: " + dataSnapshot);

                if (!dataSnapshot.exists()) {
                    future.completeExceptionally(new Exception("No data found in Firebase"));
                    return;
                }

                for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                    BusStop busStop = new BusStop();
                    busStop.setId(stopSnapshot.child("id").getValue(String.class));
                    busStop.setName(stopSnapshot.child("name").getValue(String.class));
                    busStop.setLatitude(stopSnapshot.child("latitude").getValue(Double.class));
                    busStop.setLongitude(stopSnapshot.child("longitude").getValue(Double.class));
                    busStop.setRadius(stopSnapshot.child("radius").getValue(Integer.class));

                    List<BusPoint> subBusStops = new ArrayList<>();
                    DataSnapshot busPointsSnapshot = stopSnapshot.child("buspoints");
                    for (DataSnapshot busPointSnapshot : busPointsSnapshot.getChildren()) {
                        BusPoint subBusStop = new BusPoint();
                        subBusStop.setId(busPointSnapshot.child("id").getValue(String.class));
                        subBusStop.setName(busPointSnapshot.child("name").getValue(String.class));
                        subBusStop.setLatitude(busPointSnapshot.child("latitude").getValue(Double.class));
                        subBusStop.setLongitude(busPointSnapshot.child("longitude").getValue(Double.class));

                        subBusStops.add(subBusStop);
                    }

                    busStop.setBusPoints(subBusStops);
                    busStopsList.add(busStop);
                }

                logger.info("Bus stops list: " + busStopsList);
                future.complete(busStopsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new Exception("Error fetching bus stops: " + databaseError.getMessage()));
            }
        });

        return future;
    }

    // Fetching places from Firebase
    public CompletableFuture<List<Place>> fetchPlaces() {
        CompletableFuture<List<Place>> future = new CompletableFuture<>();
        List<Place> placesList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("places");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("Firebase Data Snapshot for places: " + dataSnapshot);

                if (!dataSnapshot.exists()) {
                    future.completeExceptionally(new Exception("No places data found in Firebase"));
                    return;
                }

                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    Place place = new Place();
                    place.setId(placeSnapshot.child("id").getValue(String.class));
                    place.setName(placeSnapshot.child("name").getValue(String.class));
                    place.setLatitude(placeSnapshot.child("latitude").getValue(Double.class));
                    place.setLongitude(placeSnapshot.child("longitude").getValue(Double.class));

                    placesList.add(place);
                }

                logger.info("Places list: " + placesList);
                future.complete(placesList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new Exception("Error fetching places: " + databaseError.getMessage()));
            }
        });

        return future;
    }

    // Add places within the radius of bus stops as bus points
    public CompletableFuture<Void> addBusPointsToBusStops(List<BusStop> busStops, List<Place> places) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            for (BusStop busStop : busStops) {
                for (Place place : places) {
                    // Check if the place is within the radius of the bus stop
                    if (GeoUtils.isWithinRadius(busStop.getLatitude(), busStop.getLongitude(), place.getLatitude(), place.getLongitude(), busStop.getRadius())) {
                        // Add the place as a bus point
                        busStop.addBusPoint(place);
                    }
                }
                // After processing all places, update the bus stop in Firebase
                updateBusStop(busStop);
            }

            future.complete(null); // Done with adding bus points and updating bus stops
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    private void updateBusStop(BusStop busStop) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("busStops").child(busStop.getId());

        // Prepare a map of the fields you want to update
        Map<String, Object> busStopUpdates = new HashMap<>();
        busStopUpdates.put("name", busStop.getName());
        busStopUpdates.put("latitude", busStop.getLatitude());
        busStopUpdates.put("longitude", busStop.getLongitude());
        busStopUpdates.put("radius", busStop.getRadius());

        // If busPoints is updated, include that as well
        if (busStop.getBusPoints() != null) {
            List<Map<String, Object>> busPointUpdates = new ArrayList<>();
            for (BusPoint busPoint : busStop.getBusPoints()) {
                Map<String, Object> busPointMap = new HashMap<>();
                busPointMap.put("id", busPoint.getId());
                busPointMap.put("name", busPoint.getName());
                busPointMap.put("latitude", busPoint.getLatitude());
                busPointMap.put("longitude", busPoint.getLongitude());
                busPointUpdates.add(busPointMap);
            }
            busStopUpdates.put("buspoints", busPointUpdates);
        }

        // Update the database with the map of values, using CompletionListener to handle the callback
        ref.updateChildren(busStopUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    logger.severe("Failed to update bus stop: " + databaseError.getMessage());
                } else {
                    logger.info("Bus stop updated successfully");
                }
            }
        });
    }


}
