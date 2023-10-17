package com.example.m_hike.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "m_hike.db";
    // Built-in class for representing db manipulation
    private final SQLiteDatabase database;
    public final SQLiteDatabase getDatabase(){
        return this.database;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.database = getWritableDatabase();
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DIFFICULTY.DifficultyEntry.CREATE_QUERY);

        // Seed the table
        seedDifficulties(db);

        db.execSQL(HIKE.HikeEntry.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table IF EXISTS " + HIKE.HikeEntry.TABLE_NAME);
        db.execSQL("DROP table IF EXISTS " + DIFFICULTY.DifficultyEntry.TABLE_NAME);
        Log.w(this.getClass().getName(), db + " database upgrade to version " + newVersion);

        onCreate(db);
    }

    public long insertHike(String name, String location, Date date) {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(HIKE.HikeEntry.NAME_COLUMN_NAME, name);
        rowValues.put(HIKE.HikeEntry.LOCATION_COLUMN_NAME, location);
//        rowValues.put(LOCATION_COLUMN_NAME, location);

        return database.insertOrThrow(HIKE.HikeEntry.TABLE_NAME, null, rowValues);
    }

    public String getHikes() {
//        String query = "SELECT hikes.id, hikes.name, hikes.location, difficulties.name FROM " + HIKE.HikeEntry.TABLE_NAME + " INNER JOIN " + DIFFICULTY.DifficultyEntry.TABLE_NAME + " ON hikes." + HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME + " = difficulties." + DIFFICULTY.DifficultyEntry.ID_COLUMN_NAME;
        String query = "SELECT hikes.id, hikes.name, hikes.location, difficulties.name FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id";
        Cursor res = database.rawQuery(query , null);
        String resultText = "";

        if (res.moveToFirst()) {
            do {
                int id = res.getInt(0);
                String name = res.getString(1);
                String location = res.getString(2);
                String difficulty = res.getString(3);
                resultText += id + " " + name  + " " + location + " " + difficulty + "\n";
            } while (res.moveToNext());
        }

        res.close();

        return resultText;
    }

    private void seedDifficulties(SQLiteDatabase sqLiteDatabase) {
        String[] difficulties = new String[] {
                "Very Easy",
                "Easy",
                "Medium",
                "Hard",
                "Extreme"
        };

        for (String difficulty : difficulties) {
            sqLiteDatabase.execSQL("INSERT INTO " + DIFFICULTY.DifficultyEntry.TABLE_NAME + " (" + DIFFICULTY.DifficultyEntry.NAME_COLUMN_NAME + ") VALUES ('" + difficulty + "')");
        }
    }
}
