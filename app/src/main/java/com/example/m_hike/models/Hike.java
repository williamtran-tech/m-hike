package com.example.m_hike.models;

import java.util.Date;

public class Hike {
    private int id;
    private String name;
    private String location;
    private Date date;
    private boolean availableParking;
    private Float duration;
    private String description;
    private Integer difficulty;

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

    public String getDescription() {
        return description;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void createHike(String name, String location, Date date, boolean availableParking, Float duration, String description, Integer difficulty) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.availableParking = availableParking;
        this.duration = duration;
        this.description = description;
        this.difficulty = difficulty;
    }

    public void updateHike(String name, String location, Date date, boolean availableParking, Float duration, String description, Integer difficulty) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.availableParking = availableParking;
        this.duration = duration;
        this.description = description;
        this.difficulty = difficulty;
    }

}
