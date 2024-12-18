package com.adijith.mywebapp.model;

import java.util.ArrayList;
import java.util.List;

public class BusStop {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private int radius;
    private List<BusPoint> busPoints;

    // No-argument constructor for Firebase
    public BusStop() {
        this.busPoints = new ArrayList<>();
    }

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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public List<BusPoint> getBusPoints() {
        return busPoints;
    }

    public void setBusPoints(List<BusPoint> busPoints) {
        this.busPoints = busPoints;
    }

    // Add toString method to display the object in a more readable format
    @Override
    public String toString() {
        return "BusStop{id='" + id + "', name='" + name + "', latitude=" + latitude + ", longitude=" + longitude + ", radius=" + radius + ", busPoints=" + busPoints + "}";
    }

    // Add BusPoint from a Place object
    public void addBusPoint(Place place) {
        BusPoint busPoint = new BusPoint();
        busPoint.setId(place.getId());
        busPoint.setName(place.getName());
        busPoint.setLatitude(place.getLatitude());
        busPoint.setLongitude(place.getLongitude());

        this.busPoints.add(busPoint);  // Add new BusPoint to the list
    }
}
