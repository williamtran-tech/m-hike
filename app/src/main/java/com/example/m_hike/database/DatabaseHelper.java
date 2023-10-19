package com.example.m_hike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DIFFICULTY.DifficultyEntry.CREATE_QUERY);
        db.execSQL(HIKE.HikeEntry.CREATE_QUERY);
        Log.d("Create Query", HIKE.HikeEntry.TABLE_NAME);
        Log.d("Database", "Created");
        seedDifficulties(db);
//        Log.d("Get Difficulties", getDifficulties(db).toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table IF EXISTS " + HIKE.HikeEntry.TABLE_NAME);
        db.execSQL("DROP table IF EXISTS " + DIFFICULTY.DifficultyEntry.TABLE_NAME);
        seedDifficulties(db);
        Log.w(this.getClass().getName(), db + " database upgrade to version " + newVersion);

        onCreate(db);
    }

    public Hike insertHike(String name, Date date, String location, boolean availableParking, Integer difficulty, Float duration, Float distance) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(HIKE.HikeEntry.NAME_COLUMN_NAME, name);
        rowValues.put(HIKE.HikeEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(HIKE.HikeEntry.LOCATION_COLUMN_NAME, location);
        rowValues.put(HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, availableParking);
        rowValues.put(HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, difficulty);
        rowValues.put(HIKE.HikeEntry.DURATION_COLUMN_NAME, duration);
        rowValues.put(HIKE.HikeEntry.DISTANCE_COLUMN_NAME, distance);

        long res = database.insertOrThrow(HIKE.HikeEntry.TABLE_NAME, null, rowValues);

        return new Hike((int) res, name, location, date.toString(), availableParking, duration, distance, new Difficulty(difficulty, null));
    }

    public ArrayList<Hike> getHikes() throws ParseException {
        String query = "SELECT hikes.id, hikes.name, hikes.location, hikes.date, hikes.availableParking, hikes.duration, hikes.distance, difficulties.id, difficulties.name FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id ORDER BY strftime('%s', hikes.date) DESC";
        Cursor res = database.rawQuery(query , null);
        String resultText = "";
        ArrayList<Hike> hikesList = new ArrayList<Hike>();

        if (res.moveToFirst()) {
            do {
                int id = res.getInt(0);
                String name = res.getString(1);
                String location = res.getString(2);
                String date = res.getString(3);
                boolean parkingAvailable = res.getInt(4) == 1;
                Float duration = res.getFloat(5);
                Float distance = res.getFloat(6);
                int difficultyId = res.getInt(7);
                String difficultyName = res.getString(8);
                Difficulty difficulty = new Difficulty(difficultyId, difficultyName);
//          Integer id, String name, String location, String date, boolean availableParking, Float duration, Float distance, Difficulty difficulty
                Hike hike = new Hike(id, name, location, date, parkingAvailable, duration, distance, difficulty);
                hikesList.add(hike);
            } while (res.moveToNext());
        }
        res.close();
        return hikesList;
    }
    public Hike getHike(Integer id) throws ParseException {
        String query = "SELECT * FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id WHERE hikes.id = " + id;
        Cursor res = database.rawQuery(query , null);
        Hike hike = new Hike(res.getInt(0), res.getString(1), res.getString(2), res.getString(3), res.getInt(4) == 1, res.getFloat(5), res.getFloat(6), new Difficulty(res.getInt(7), res.getString(8)));
        res.close();
        return hike;
    }
    private void seedDifficulties(SQLiteDatabase db) {
        String[] inserts = DIFFICULTY.DifficultyEntry.Seed();
        Log.d("DifInserts", inserts.toString());
        for (String insert : inserts) {
            Log.d("Diff", insert);
            db.execSQL(insert);
        }
    }
    public ArrayList<Difficulty> getDifficulties() {
        String query = DIFFICULTY.DifficultyEntry.getDifficulties();
        Cursor res = database.rawQuery(query, null);
        ArrayList<Difficulty> difficultiesList = new ArrayList<Difficulty>();
        ArrayList<Difficulty> result = new ArrayList<Difficulty>(){};
        if (res.moveToFirst()) {
            do {
                int id = res.getInt(0);
                String name = res.getString(1);
                Difficulty diff = new Difficulty(id, name);
                difficultiesList.add(diff);
            } while (res.moveToNext());
        }

        res.close();

        return difficultiesList;
    }

}
