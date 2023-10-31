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

    // HIKE CRUD
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
        String query = "SELECT hikes.id, hikes.name, hikes.location, hikes.date, hikes.availableParking, hikes.duration, hikes.distance, difficulties.id, difficulties.name, hikes.description FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id WHERE deletedAt IS NULL ORDER BY hikes.date ASC";
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
                String description = res.getString(9);
//          Integer id, String name, String location, String date, boolean availableParking, Float duration, Float distance, Difficulty difficulty
                Hike hike = new Hike(id, name, location, date, parkingAvailable, duration, distance, difficulty, description);
                hikesList.add(hike);
            } while (res.moveToNext());
        }
        res.close();
        return hikesList;
    }
    public static ArrayList<Hike> getHikesByDifficulty(Difficulty difficultyObj) throws ParseException {
        String query = "SELECT hikes.id, hikes.name, hikes.location, hikes.date, hikes.availableParking, hikes.duration, hikes.distance, difficulties.id, difficulties.name, hikes.description FROM hikes INNER JOIN difficulties ON hikes.difficultyId = difficulties.id WHERE deletedAt IS NULL AND difficulties.id = " + difficultyObj.getId() +" ORDER BY hikes.date ASC";
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
                String description = res.getString(9);
//          Integer id, String name, String location, String date, boolean availableParking, Float duration, Float distance, Difficulty difficulty
                Hike hike = new Hike(id, name, location, date, parkingAvailable, duration, distance, difficulty, description);
                hikesList.add(hike);
            } while (res.moveToNext());
        }
        res.close();
        return hikesList;
    }
    public static Hike updateHike(Integer id, String name, Date date, String location, boolean availableParking, Float duration, Float distance, Integer difficultyId, String description,Date deletedAt) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(HIKE.HikeEntry.NAME_COLUMN_NAME, name);
        rowValues.put(HIKE.HikeEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(HIKE.HikeEntry.LOCATION_COLUMN_NAME, location);
        rowValues.put(HIKE.HikeEntry.AVAILABLE_PARKING_COLUMN_NAME, availableParking);
        rowValues.put(HIKE.HikeEntry.DIFFICULTY_COLUMN_NAME, difficultyId);
        rowValues.put(HIKE.HikeEntry.DURATION_COLUMN_NAME, duration);
        rowValues.put(HIKE.HikeEntry.DISTANCE_COLUMN_NAME, distance);
        rowValues.put(HIKE.HikeEntry.DESCRIPTION_COLUMN_NAME, description);
        if (deletedAt != null) {
            Log.d("DeletedAt", deletedAt.toString());
            rowValues.put(HIKE.HikeEntry.DELETEDAT_COLUMN_NAME, deletedAt.toString());
        } else {
            rowValues.putNull(HIKE.HikeEntry.DELETEDAT_COLUMN_NAME);
        }
        long res = database.update(HIKE.HikeEntry.TABLE_NAME, rowValues, "id = " + id, null);
        Log.d("Database - Update: ", name + id);

        Difficulty diff = DatabaseHelper.getDifficulty(difficultyId);
        return new Hike((int) res, name, location, date.toString(), availableParking, duration, distance, diff, description);
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
                Log.d("Database - Get Hike: ", id + " " + hike.getName());
            }
            cursor.close(); // Don't forget to close the cursor when you're done with it
        }
        return hike;
    }
    public static Hike deleteHike(Integer id) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(HIKE.HikeEntry.DELETEDAT_COLUMN_NAME, new Date().toString());

        long res = database.update(HIKE.HikeEntry.TABLE_NAME, rowValues, "id = " + id, null);

        return getHike(id);
    }
    public static boolean forceDeleteHike(Integer id) {
        long hike = database.delete(HIKE.HikeEntry.TABLE_NAME, "id = " + id, null);
        // Delete observations associated with hike
        long obs = database.delete(OBSERVATION.ObservationEntry.TABLE_NAME, "hike_id = " + id, null);
        return (hike > 0 && obs > 0);
    }

    // DIFFICULTY INITIALIZATION
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

    public static Difficulty getDifficulty(Integer id) {
        ContentValues rowValues = new ContentValues(); // new row object
        String query = "SELECT id, name from difficulties where id = " + id;
        Cursor cursor = database.rawQuery(query , null);
        Difficulty difficulty = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Now, the cursor is positioned at the first row
                // Set the values
                String name = cursor.getString(1);
                difficulty = new Difficulty(id, name);
            }
            cursor.close(); // Don't forget to close the cursor when you're done with it
        }
        return difficulty;
    }

    // OBSERVATION CRUD
    public static Observation insertObservation(String caption, Date date, byte[] image, double longitude, double latitude, Integer hikeId) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(OBSERVATION.ObservationEntry.CAPTION_COLUMN_NAME, caption);
        rowValues.put(OBSERVATION.ObservationEntry.OBSERVATION_COLUMN_NAME, image);
        rowValues.put(OBSERVATION.ObservationEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(OBSERVATION.ObservationEntry.HIKE_ID_COLUMN_NAME, hikeId);
        rowValues.put(OBSERVATION.ObservationEntry.LATITUDE_COLUMN_NAME, latitude);
        rowValues.put(OBSERVATION.ObservationEntry.LONGITUDE_COLUMN_NAME, longitude);

        long res = database.insertOrThrow(OBSERVATION.ObservationEntry.TABLE_NAME, null, rowValues);

        return new Observation((int) res, caption, date.toString(), image, longitude, latitude, hikeId);
    }

    public static ArrayList<Observation> getObservations(Integer hikeId) {
        String query = "SELECT observations.id, observations.caption, observations.date, observations.observation, observations.latitude, observations.longitude, observations.hike_id, hikes.name FROM observations INNER JOIN hikes ON observations.hike_id = hikes.id WHERE observations.deletedAt IS NULL AND observations.hike_id = " + hikeId + " ORDER BY observations.date DESC";
        Cursor res = database.rawQuery(query , null);
        ArrayList<Observation> observationsList = new ArrayList<Observation>();

        if (res.moveToFirst()) {
            do {
                int id = res.getInt(0);
                String caption = res.getString(1);
                String date = res.getString(2);
                byte[] image = res.getBlob(3);
                double latitude = res.getDouble(4);
                double longitude = res.getDouble(5);
                int hId = res.getInt(6);
                Observation observation = null;
                try {
                    observation = new Observation(id, caption, date, image, longitude, latitude, hikeId);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                observationsList.add(observation);
            } while (res.moveToNext());
        }
        res.close();
        return observationsList;
    }
    public static Observation getObservation(Integer id) {
        ContentValues rowValues = new ContentValues(); // new row object
        String query = "SELECT observations.id, observations.caption, observations.date, observations.observation, observations.latitude, observations.longitude, observations.hike_id, hikes.name FROM observations INNER JOIN hikes ON observations.hike_id = hikes.id WHERE observations.id = " + id;
        Cursor cursor = database.rawQuery(query , null);
        Observation observation = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Now, the cursor is positioned at the first row
                // Set the values
                String caption = cursor.getString(1);
                String date = cursor.getString(2);
                byte[] image = cursor.getBlob(3);
                double latitude = cursor.getDouble(4);
                double longitude = cursor.getDouble(5);
                int hikeId = cursor.getInt(6);

                try {
                    observation = new Observation(id, caption, date, image, longitude, latitude,  hikeId);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            cursor.close(); // Don't forget to close the cursor when you're done with it
        }
        return observation;

    }
    public static Observation deleteObservation(Integer id) {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(OBSERVATION.ObservationEntry.DELETEDAT_COLUMN_NAME, new Date().toString());

        long res = database.update(OBSERVATION.ObservationEntry.TABLE_NAME, rowValues, "id = " + id, null);

        return getObservation(id);
    }

    public static boolean forceDeleteObservation(Integer id) {
        long res = database.delete(OBSERVATION.ObservationEntry.TABLE_NAME, "id = " + id, null);
        return res > 0;
    }

    public static Observation updateObservation(Integer id, String caption, Date date, byte[] image, double longitude, double latitude, Integer hikeId, Date deletedAt ) throws ParseException {
        ContentValues rowValues = new ContentValues(); // new row object
        Log.d("Date", date.toString());
        if (deletedAt != null) {
            Log.d("WTA", "ASd");
            rowValues.put(OBSERVATION.ObservationEntry.DELETEDAT_COLUMN_NAME, deletedAt.toString());
        } else {
            rowValues.putNull(OBSERVATION.ObservationEntry.DELETEDAT_COLUMN_NAME);
        }
        rowValues.put(OBSERVATION.ObservationEntry.CAPTION_COLUMN_NAME, caption);
        rowValues.put(OBSERVATION.ObservationEntry.OBSERVATION_COLUMN_NAME, image);
        rowValues.put(OBSERVATION.ObservationEntry.DATE_COLUMN_NAME, date.toString());
        rowValues.put(OBSERVATION.ObservationEntry.HIKE_ID_COLUMN_NAME, hikeId);
        rowValues.put(OBSERVATION.ObservationEntry.LATITUDE_COLUMN_NAME, latitude);
        rowValues.put(OBSERVATION.ObservationEntry.LONGITUDE_COLUMN_NAME, longitude);

        long res = database.update(OBSERVATION.ObservationEntry.TABLE_NAME, rowValues, "id = " + id, null);

        return new Observation(id, caption, date.toString(), image, longitude, latitude, hikeId);
    }

    public static boolean resetDatabase() {
        database.execSQL("DROP table IF EXISTS " + HIKE.HikeEntry.TABLE_NAME);
        database.execSQL("DROP table IF EXISTS " + OBSERVATION.ObservationEntry.TABLE_NAME);

        database.execSQL(HIKE.HikeEntry.CREATE_QUERY);
        database.execSQL(OBSERVATION.ObservationEntry.CREATE_QUERY);
        return true;
    }
}
