package com.adijith.mywebapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PassengerLocation {
    private Location boarding;
    private Location gettingOff;

    // Default constructor required for Firebase
    public PassengerLocation() {
    }

    // Constructor with parameters
    public PassengerLocation(Location boarding, Location gettingOff) {
        this.boarding = boarding;
        this.gettingOff = gettingOff;
    }

    // Getter and Setter for boarding
    public Location getBoarding() {
        return boarding;
    }

    public void setBoarding(Location boarding) {
        this.boarding = boarding;
    }

    // Getter and Setter for gettingOff
    public Location getGettingOff() {
        return gettingOff;
    }

    public void setGettingOff(Location gettingOff) {
        this.gettingOff = gettingOff;
    }
}



