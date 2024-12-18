package com.adijith.mywebapp.model;

public class HeatMapLocation {
    private double latitude;
    private double longitude;
    private String busStopName;
    private int count;

    public HeatMapLocation(double latitude, double longitude, String busStopName, int count) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.busStopName = busStopName;
        this.count = count;
    }

    // Getter and Setter for latitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter and Setter for longitude
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Getter and Setter for busStopName
    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    // Getter and Setter for count
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "HeatMapLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", busStopName='" + busStopName + '\'' +
                ", count=" + count +
                '}';
    }
}
