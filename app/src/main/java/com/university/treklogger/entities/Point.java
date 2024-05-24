package com.university.treklogger.entities;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Point {
    private GeoPoint coordinates;
    private String image;
    private String name;

    public Point() {
        // No-arg constructor required for Firestore
    }

    public Point(double latitude, double longitude, String image, String name) {
        this.coordinates = new GeoPoint(latitude, longitude);
        this.image = image;
        this.name = name;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setImageUrl(String path) {
        image = path;
    }
}
