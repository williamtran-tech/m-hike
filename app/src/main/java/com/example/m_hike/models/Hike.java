package com.example.m_hike.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Hike {
    private int id;
    private String name;
    private String location;
    private Date date;
    private boolean availableParking;
    private Float duration;
    private Float distance;
    private Difficulty difficulty;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public boolean isAvailableParking() {
        return availableParking;
    }

    public Float getDuration() {
        return duration;
    }

    public Float getDistance() {
        return distance;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Hike(Integer id, String name, String location, String date, boolean availableParking, Float duration, Float distance, Difficulty difficulty) throws ParseException {
        this.id = id;
        this.name = name;
        this.location = location;
        // Use the correct date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        this.date = dateFormat.parse(date);
        this.availableParking = availableParking;
        this.duration = duration;
        this.distance = distance;
        this.difficulty = difficulty;
    }

    public void updateHike(String name, String location, Date date, boolean availableParking, Float duration, Float distance, Difficulty difficulty) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.availableParking = availableParking;
        this.duration = duration;
        this.distance = distance;
        this.difficulty = difficulty;
    }

}
