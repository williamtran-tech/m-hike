package com.example.m_hike.database;

import android.provider.BaseColumns;

public final class OBSERVATION {
    // To prevent someone from accidentally instantiating the Difficulty class,
    // make the constructor private.
    private OBSERVATION() {}
    public static class ObservationEntry implements BaseColumns {
        public static final String TABLE_NAME = "observations";
        public static final String ID_COLUMN_NAME = "id";
        public static final String CAPTION_COLUMN_NAME = "caption";
        public static final String OBSERVATION_COLUMN_NAME = "observation";
        public static final String DATE_COLUMN_NAME = "date";
        public static final String HIKE_ID_COLUMN_NAME = "hike_id";
        public static final String LATITUDE_COLUMN_NAME = "latitude";
        public static final String LONGITUDE_COLUMN_NAME = "longitude";

        public static final String CREATE_QUERY = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL, " +
                        "%s BLOB, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER, %s INTEGER, " +
                        "FOREIGN KEY (%s) REFERENCES hikes(id)" +
                        ")", OBSERVATION.ObservationEntry.TABLE_NAME, OBSERVATION.ObservationEntry.ID_COLUMN_NAME, ObservationEntry.CAPTION_COLUMN_NAME, ObservationEntry.OBSERVATION_COLUMN_NAME, ObservationEntry.DATE_COLUMN_NAME, ObservationEntry.HIKE_ID_COLUMN_NAME, ObservationEntry.LATITUDE_COLUMN_NAME, ObservationEntry.LONGITUDE_COLUMN_NAME, ObservationEntry.HIKE_ID_COLUMN_NAME
        );
        public static String getObservations() {
            return "SELECT * FROM " + OBSERVATION.ObservationEntry.TABLE_NAME;
        }
    }
}