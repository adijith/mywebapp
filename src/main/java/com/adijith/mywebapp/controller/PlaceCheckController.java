package com.adijith.mywebapp.controller;

import com.adijith.mywebapp.service.PlaceCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class PlaceCheckController {

    @Autowired
    private PlaceCheckService placeCheckService;

    // Endpoint to check and add bus points to bus stops
    @GetMapping("/checkAndAddBusPoints")
    public CompletableFuture<String> checkAndAddBusPoints() {
        return placeCheckService.checkAndAddBusPoints()
                .thenApply(v -> "Bus points added successfully")
                .exceptionally(e -> "Error adding bus points: " + e.getMessage());
    }
}
