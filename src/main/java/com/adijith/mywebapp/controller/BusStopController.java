package com.adijith.mywebapp.controller;

import com.adijith.mywebapp.firebase.FirebaseDatabaseService;
import com.adijith.mywebapp.model.BusStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
public class BusStopController {

    private static final Logger logger = Logger.getLogger(BusStopController.class.getName());

    @Autowired
    private FirebaseDatabaseService firebaseDatabaseService;

    @GetMapping("/busStops")
    public String getBusStops(Model model) {
        try {
            // Fetch bus stops data asynchronously
            List<BusStop> busStops = firebaseDatabaseService.fetchBusStops().get();
            logger.info("Fetched bus stops: " + busStops); // Log the fetched data

            if (busStops.isEmpty()) {
                model.addAttribute("message", "No bus stops found.");
            } else {
                model.addAttribute("busStops", busStops);
            }
        } catch (InterruptedException | ExecutionException e) {
            model.addAttribute("error", "Error fetching bus stops: " + e.getMessage());
            logger.severe("Error fetching bus stops: " + e.getMessage()); // Log the error
        }
        return "busStops";  // Return the JSP view name
    }
}
