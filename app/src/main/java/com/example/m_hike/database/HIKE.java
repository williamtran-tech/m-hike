package com.example.m_hike.database;

import android.provider.BaseColumns;

public final class HIKE implements BaseColumns {
    private HIKE() {
    }

    public static class HikeEntry {
        public static final String TABLE_NAME = "hikes";
        public static final String ID_COLUMN_NAME = "id";
        public static final String NAME_COLUMN_NAME = "name";
        public static final String LOCATION_COLUMN_NAME = "location";
        public static final String DATE_COLUMN_NAME = "date";
        public static final String AVAILABLE_PARKING_COLUMN_NAME = "availableParking";
        public static final String DURATION_COLUMN_NAME = "duration";
        public static final String DISTANCE_COLUMN_NAME = "distance";
        public static final String DESCRIPTION_COLUMN_NAME = "description";
        public static final String DIFFICULTY_COLUMN_NAME = "difficultyId";
        public static final String DELETEDAT_COLUMN_NAME = "deletedAt";
        ;
        public static final String CREATE_QUERY = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s CHECK (availableParking IN (0, 1)), " +
                        "%s REAL, " +
                        "%s REAL, " +
                        "%s TEXT, " +
                        "%s TEXT," +
                        "%s INTEGER DEFAULT 1 NOT NULL, " +
                        "FOREIGN KEY (%s) REFERENCES difficulties(id)" +
                        ")", HIKE.HikeEntry.TABLE_NAME, HIKE.HikeEntry.ID_COLUMN_NAME, HIKE.HikeEntry.NAME_COLUMN_NAME, HIKE.HikeEntry.LOCATION_COLUMN_NAME, HIKE.HikeEntry.DATE_COLUMN_NAME, HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, HIKE.HikeEntry.DURATION_COLUMN_NAME, HikeEntry.DISTANCE_COLUMN_NAME, HIKE.HikeEntry.DESCRIPTION_COLUMN_NAME, HIKE.HikeEntry.DELETEDAT_COLUMN_NAME, HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME);
    }
}