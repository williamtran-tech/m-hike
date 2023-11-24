package com.example.m_hike.models;

import com.example.m_hike.database.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Observation {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Hike getHike() {
        return hike;
    }

    public void setHike(Hike hike) {
        this.hike = hike;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    private int id;
    private String caption;
    private Date date;
    private Hike hike;
    private byte[] image;
    private double latitude;
    private double longitude;

    public Date getDeletedAt() {
        return deletedAt;
    }

    private Date deletedAt;


    public Observation(Integer id, String caption, String date, byte[] image, double longitude, double latitude, Integer hikeId) throws ParseException {
        this.id = id;
        this.caption = caption;
        // Use the correct date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        this.date = dateFormat.parse(date);
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hike = DatabaseHelper.getHike(hikeId);
    }

}
