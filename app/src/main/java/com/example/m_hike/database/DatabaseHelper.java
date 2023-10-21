package com.example.m_hike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.m_hike.models.Difficulty;
import com.example.m_hike.models.Hike;
import com.example.m_hike.models.Observation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "m_hike.db";
    // Built-in class for representing db manipulation
    private static SQLiteDatabase database;
    public final SQLiteDatabase getDatabase(){
        return database;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DIFFICULTY.DifficultyEntry.CREATE_QUERY);
        db.execSQL(HIKE.HikeEntry.CREATE_QUERY);
        db.execSQL(OBSERVATION.ObservationEntry.CREATE_QUERY);
        Log.d("Create Query", HIKE.HikeEntry.TABLE_NAME);
        Log.d("Database", "Created");
        seedDifficulties(db);
//        Log.d("Get Difficulties", getDifficulties(db).toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table IF EXISTS " + HIKE.HikeEntry.TABLE_NAME);
        db.execSQL("DROP table IF EXISTS " + DIFFICULTY.DifficultyEntry.TABLE_NAME);
        db.execSQL("DROP table IF EXISTS " + OBSERVATION.ObservationEntry.TABLE_NAME);
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

        return new Hike((int) res, name, location, date.toString(), availableParking, duration, distance, new Difficulty(difficulty, null), null);
    }
    public static ArrayList<Hike> getHikes() throws ParseException {
        String query = "SELECT hikes.id, hikes.name, hikes.location, hikes.date, hikes.availableParking, hikes.duration, hikes.distance, difficulties.id, difficulties.name FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id ORDER BY strftime('%s', hikes.date) DESC";
        Cursor res = database.rawQuery(query , null);
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
                Hike hike = new Hike(id, name, location, date, parkingAvailable, duration, distance, difficulty, null);
                hikesList.add(hike);
            } while (res.moveToNext());
        }
        res.close();
        return hikesList;
    }
    public static Hike updateHike(Integer id, String name, Date date, String location, boolean availableParking, Float duration, Float distance, Difficulty difficulty, String description) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(HIKE.HikeEntry.NAME_COLUMN_NAME, name);
        rowValues.put(HIKE.HikeEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(HIKE.HikeEntry.LOCATION_COLUMN_NAME, location);
        rowValues.put(HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, availableParking);
        rowValues.put(HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, difficulty.getId());
        rowValues.put(HIKE.HikeEntry.DURATION_COLUMN_NAME, duration);
        rowValues.put(HIKE.HikeEntry.DISTANCE_COLUMN_NAME, distance);
        rowValues.put(HIKE.HikeEntry.DESCRIPTION_COLUMN_NAME, description);

        long res = database.update(HIKE.HikeEntry.TABLE_NAME, rowValues, "id = " + id, null);

        return new Hike((int) res, name, location, date.toString(), availableParking, duration, distance, difficulty, description);
    }
    public static Hike getHike(Integer id) throws ParseException {
        String query = "SELECT hikes.id, hikes.name, hikes.location, hikes.date, hikes.availableParking, hikes.duration, hikes.distance, hikes.difficultyId, difficulties.name, hikes.description  FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id WHERE hikes.id = " + id;
        Cursor res = database.rawQuery(query , null);
        Cursor cursor = res;// Your cursor object

        Hike hike = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Now, the cursor is positioned at the first row
                // Set the values
                int hikeId = res.getInt(0);
                String name = res.getString(1);
                String location = res.getString(2);
                String date = res.getString(3);
                boolean parkingAvailable = res.getInt(4) == 1;
                Float duration = res.getFloat(5);
                Float distance = res.getFloat(6);
                Difficulty difficulty = new Difficulty(res.getInt(7), res.getString(8));
                String description = res.getString(9);

                hike = new Hike(hikeId, name, location, date, parkingAvailable, duration, distance, difficulty, description);
            }
            cursor.close(); // Don't forget to close the cursor when you're done with it
        }
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
    public Observation insertObservation(String caption, Date date, byte[] image, double longitude, double latitude, Hike hike) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(OBSERVATION.ObservationEntry.CAPTION_COLUMN_NAME, caption);
        rowValues.put(OBSERVATION.ObservationEntry.OBSERVATION_COLUMN_NAME, image);
        rowValues.put(OBSERVATION.ObservationEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(OBSERVATION.ObservationEntry.HIKE_ID_COLUMN_NAME, hike.getId());
        rowValues.put(OBSERVATION.ObservationEntry.LATITUDE_COLUMN_NAME, latitude);
        rowValues.put(OBSERVATION.ObservationEntry.LONGITUDE_COLUMN_NAME, longitude);

        long res = database.insertOrThrow(OBSERVATION.ObservationEntry.TABLE_NAME, null, rowValues);

        return new Observation((int) res, caption, date.toString(), image, longitude, latitude, hike);
    }
}
