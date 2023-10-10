package com.example.m_hike.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class HIKE implements BaseColumns {
    private HIKE() {}

    public static class HikeEntry {
        public static final String TABLE_NAME = "hikes";
        public static final String ID_COLUMN_NAME = "id";
        public static final String NAME_COLUMN_NAME = "name";
        public static final String LOCATION_COLUMN_NAME = "location";
        public static final String DATE_COLUMN_NAME = "date";
        public static final String AVAILABLE_PARKING_COLUMN_NAME = "availableParking";
        public static final String DURATION_COLUMN_NAME = "duration";
        public static final String DESCRIPTION_COLUMN_NAME = "description";
        public static final String DIFFICULTY_COLUMN_NAME = "difficultyId";

        public static final String CREATE_QUERY = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s CHECK (availableParking IN (0, 1)), " +
                        "%s REAL, " +
                        "%s TEXT, " +
                        "%s INTEGER DEFAULT 1 NOT NULL, " +
                        "FOREIGN KEY (%s) REFERENCES difficulties(id)" +
                        ")", HIKE.HikeEntry.TABLE_NAME, HIKE.HikeEntry.ID_COLUMN_NAME, HIKE.HikeEntry.NAME_COLUMN_NAME, HIKE.HikeEntry.LOCATION_COLUMN_NAME, HIKE.HikeEntry.DATE_COLUMN_NAME, HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, HIKE.HikeEntry.DURATION_COLUMN_NAME, HIKE.HikeEntry.DESCRIPTION_COLUMN_NAME, HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME);
    }


    // Get all hikes
    public class HikeOperator {
        public List<HikeData> getHikes(SQLiteDatabase db) {
            List<HikeData> resultList = new ArrayList<HikeData>();
            String query = String.format(
                    "SELECT %s, %s, %s, %s, %s, %s, %s, %s FROM %s INNER JOIN %s ON %s.%s = %s.%s",
                    HIKE.HikeEntry.ID_COLUMN_NAME, HIKE.HikeEntry.NAME_COLUMN_NAME, HIKE.HikeEntry.LOCATION_COLUMN_NAME, HIKE.HikeEntry.DATE_COLUMN_NAME, HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, HIKE.HikeEntry.DURATION_COLUMN_NAME, HIKE.HikeEntry.DESCRIPTION_COLUMN_NAME, DIFFICULTY.DifficultyEntry.NAME_COLUMN_NAME, HIKE.HikeEntry.TABLE_NAME, DIFFICULTY.DifficultyEntry.TABLE_NAME, HIKE.HikeEntry.TABLE_NAME, HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, DIFFICULTY.DifficultyEntry.TABLE_NAME, DIFFICULTY.DifficultyEntry.ID_COLUMN_NAME);
            Cursor results = db.rawQuery(query , null);

            if (results.moveToFirst()) {
                do {
                    int id = results.getInt(0);
                    String name = results.getString(1);
                    String location = results.getString(2);
                    String difficulty = results.getString(3);
                    HikeData hike = new HikeData();
                    hike.setName(name);
                    hike.setLocation(location);
                    hike.setId(id);
                    hike.setDifficulty(difficulty);
                    resultList.add(hike);

                } while (results.moveToNext());
            }

            results.close();

            return resultList;
        };
    }
    public class HikeData {
        private Integer id;
        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public boolean isAvailableParking() {
            return availableParking;
        }

        public void setAvailableParking(boolean availableParking) {
            this.availableParking = availableParking;
        }

        public Float getDuration() {
            return duration;
        }

        public void setDuration(Float duration) {
            this.duration = duration;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getDifficultyId() {
            return difficultyId;
        }

        public void setDifficultyId(Integer difficultyId) {
            this.difficultyId = difficultyId;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        private String name;
        private String location;
        private String date;
        private boolean availableParking;
        private Float duration;
        private String description;
        private Integer difficultyId;
        private String difficulty;

        public void setName(String name) {
            this.name = name;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public Integer getId() {
            return id;
        }

    }


}
