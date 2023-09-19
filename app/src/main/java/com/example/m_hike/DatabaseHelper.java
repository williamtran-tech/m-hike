package com.example.m_hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "m_hike";
    private static final String ID_COLUMN_NAME = "personId";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String EMAIL_COLUMN_NAME = "email";
    private static final String LOCATION_COLUMN_NAME = "location";

    // Built-in class for representing db manipulation
    private final SQLiteDatabase database;

    private static final String DATABASE_CREATE_QUERY = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT)", TABLE_NAME, ID_COLUMN_NAME, NAME_COLUMN_NAME, EMAIL_COLUMN_NAME, LOCATION_COLUMN_NAME
    );
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Log.w(this.getClass().getName(), TABLE_NAME + " database upgrade to version " + newVersion);

        onCreate(db);
    }

    public long insertTest(String name, String location, String email) {
        ContentValues rowValues = new ContentValues(); // new row object
        rowValues.put(NAME_COLUMN_NAME, name);
        rowValues.put(EMAIL_COLUMN_NAME, email);
        rowValues.put(LOCATION_COLUMN_NAME, location);

        return database.insertOrThrow(TABLE_NAME, null, rowValues);
    }

    public String getTest() {
        Cursor results = database.query("tests",
                new String[] {"personId", "name", "email", "location"}, null, null, null, null, "name");

        String resultText = "";
        results.moveToFirst();

        while(!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            String email = results.getString(2);
            String location = results.getString(3);

            resultText += id + " " + name + " " + email + " " + location + "\n";
            results.moveToNext();
        }

        return resultText;
    }
}
