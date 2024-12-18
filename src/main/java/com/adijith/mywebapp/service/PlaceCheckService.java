package com.adijith.mywebapp.service;

import com.adijith.mywebapp.firebase.FirebaseDatabaseService;
import com.adijith.mywebapp.model.BusStop;
import com.adijith.mywebapp.model.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PlaceCheckService {

    @Autowired
    private FirebaseDatabaseService firebaseDatabaseService;

    // Method to check if the places are within any bus stop's radius and add them as bus points
    public CompletableFuture<Void> checkAndAddBusPoints() {
        // Fetch bus stops and places
        CompletableFuture<List<BusStop>> busStopsFuture = firebaseDatabaseService.fetchBusStops();
        CompletableFuture<List<Place>> placesFuture = firebaseDatabaseService.fetchPlaces();

        // Combine both futures and process them when both are ready
        return CompletableFuture.allOf(busStopsFuture, placesFuture)
                .thenCompose(v -> {
                    try {
                        // Get the results from the futures
                        List<BusStop> busStops = busStopsFuture.get();
                        List<Place> places = placesFuture.get();

                        // Call method to check and add bus points to bus stops
                        return firebaseDatabaseService.addBusPointsToBusStops(busStops, places);
                    } catch (Exception e) {
                        return CompletableFuture.failedFuture(e);
                    }
                });
    }
}
