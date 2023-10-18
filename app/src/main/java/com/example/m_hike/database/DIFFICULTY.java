//package com.example.m_hike.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DIFFICULTY extends SQLiteOpenHelper {
//    private static final String TABLE_NAME = "difficulties";
//    private static final String ID_COLUMN_NAME = "id";
//    private static final String NAME_COLUMN_NAME = "name";
//    private static SQLiteDatabase database;
//    public DIFFICULTY(Context context) {
//        super(context, TABLE_NAME, null, 1);
//        database = getWritableDatabase();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
//                ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                NAME_COLUMN_NAME + " STRING NOT NULL)";
//
//        db.execSQL(CREATE_TABLE_QUERY);
//        // Seed the table
//        seedDifficulties(db);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//
//    }
//
//    private void seedDifficulties(SQLiteDatabase sqLiteDatabase) {
//        String[] difficulties = new String[] {
//                "Easy",
//                "Medium",
//                "Hard"
//        };
//
//        for (String difficulty : difficulties) {
//            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME + " (" + NAME_COLUMN_NAME + ") VALUES ('" + difficulty + "')");
//        }
//    }
//}

package com.example.m_hike.database;

import android.provider.BaseColumns;

public final class DIFFICULTY {
    // To prevent someone from accidentally instantiating the Difficulty class,
    // make the constructor private.
    private DIFFICULTY() {}
    public static class DifficultyEntry implements BaseColumns {
        public static final String TABLE_NAME = "difficulties";
        public static final String ID_COLUMN_NAME = "id";
        public static final String NAME_COLUMN_NAME = "name";

        public static final String CREATE_QUERY = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s TEXT NOT NULL)", DIFFICULTY.DifficultyEntry.TABLE_NAME, DIFFICULTY.DifficultyEntry.ID_COLUMN_NAME, DIFFICULTY.DifficultyEntry.NAME_COLUMN_NAME
        );
        public static String[] Seed() {
            String[] DIFFICULTIES = new String[] {
                    "Very Easy",
                    "Easy",
                    "Medium",
                    "Hard",
                    "Extreme"
            };
            String[] res = new String[DIFFICULTIES.length];
            for (int i = 0; i < DIFFICULTIES.length; i++) {
                res[i] = ("INSERT INTO difficulties (name) VALUES (\"" + DIFFICULTIES[i] + "\")");
//                res[i] = ("INSERT INTO difficulties (name) VALUES (" + DIFFICULTIES[i] + ")");
            }

            return res;
        };
        public static String getDifficulties() {
            return "SELECT * FROM " + DIFFICULTY.DifficultyEntry.TABLE_NAME;
        }
    }
}