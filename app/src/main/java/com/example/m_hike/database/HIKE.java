package com.example.m_hike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HIKE extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "hikes";
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String LOCATION_COLUMN_NAME = "location";
    private static final String DATE_COLUMN_NAME = "date";
    private static final String AVAILABLE_PARKING_COLUMN_NAME = "availableParking";
    private static final String DURATION_COLUMN_NAME = "duration";
    private static final String DESCRIPTION_COLUMN_NAME = "description";
    private static final String DIFFICULTY_COLUMN_NAME = "difficultyId";

    private final SQLiteDatabase database;
    public HIKE(Context context) {
        super(context, TABLE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE_QUERY = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT, " +
                        "%s TEXT, " +
                        "%s DATE, " +
                        "%s CHECK (availableParking IN (0, 1)), " +
                        "%s REAL, " +
                        "%s TEXT, " +
                        "%s FOREIGN KEY (difficultyId) REFERENCES difficulties(id)", TABLE_NAME, ID_COLUMN_NAME, NAME_COLUMN_NAME, LOCATION_COLUMN_NAME, DATE_COLUMN_NAME, AVAILABLE_PARKING_COLUMN_NAME, DURATION_COLUMN_NAME, DESCRIPTION_COLUMN_NAME, DIFFICULTY_COLUMN_NAME
        );
        db.execSQL(DATABASE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
