package com.adijith.mywebapp.service;

import com.adijith.mywebapp.model.BusStop;
import com.adijith.mywebapp.firebase.FirebaseDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class BusStopService {

    @Autowired
    private FirebaseDatabaseService firebaseDatabaseService;

    public List<BusStop> getAllBusStops() {
        try {
            return firebaseDatabaseService.fetchBusStops().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching bus stops", e);
        }
    }
}
