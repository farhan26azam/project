package com.university.treklogger.entities;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Trek {
    private String title;
    private String description;
    private String coverImageUrl;
    private List<Point> points;

    public Trek(String title, String description, String coverImageUrl, List<Point> points) {
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImage() {
        return coverImageUrl;
    }

    public List<Point> getPoints() {
        return points;
    }
}
