package com.adijith.mywebapp.model;

public class BusPoint {
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    // No-argument constructor for Firebase
    public BusPoint() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Add toString method to display the object in a more readable format
    @Override
    public String toString() {
        return "BusPoint{id='" + id + "', name='" + name + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}
