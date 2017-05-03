/**
 * Add level column to table
 */

package com.example.ranendelman.minesweeper.DBLogic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ranendelman.minesweeper.GameLogic.Score;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;


import java.util.ArrayList;

/**
 * Created by ofear on 1/7/2017.
 */
public class dataBase extends SQLiteOpenHelper {

    private static final String LOG = "DB";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Single tone db parameter
    private static dataBase db;

    //Database name
    private static final String DATABASE_NAME = "Minesweeper";

    // Table Name
    private static final String RECORDS_TABLE = "records_table";

    // Score Table - column names
    private static final String KEY_ID = "id";
    private static final String NAME = "name";
    private static final String SCORE = "score";
    private static final String LEVEL = "level";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ADDRESS = "address";
    private static final String COUNTRY = "country";
    private static final String CITY = "city";
    private final int NUM_OF_RECORD = 9;

    // Score_easy table create statement
    private static final String CREATE_TABLE_EASY_SCORE = "CREATE TABLE " + RECORDS_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT,"
            + SCORE + " INTEGER,"
            + LEVEL + " TEXT,"
            + LATITUDE + " TEXT,"
            + ADDRESS + " TEXT,"
            + LONGITUDE + " TEXT,"
            + COUNTRY + " TEXT,"
            + CITY + " TEXT " + ")";


    public static synchronized dataBase getInstance(Context context) {
        if (db == null)
            db = new dataBase(context.getApplicationContext());
        return db;
    }

    /** The class constructor */
    public dataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(LOG, "Database created");
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EASY_SCORE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_EASY_SCORE);
        // create new tables
        onCreate(db);
    }

    /**
     * This method check if the score should be insert to the score table
     */
    public boolean recordCheck(int time, gameLevel level) {
        ArrayList<Score> tmpRec = getScoreTable(level);
        if (tmpRec.size() >= 10) {
            if (tmpRec.get(NUM_OF_RECORD).getScore() >= time)
                return true;
            return false;
        } else
            return true;
    }
    /**
     * This method create a new score and insert it inside the table
     */
    public long creatNewScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME, score.getName());
        values.put(SCORE, score.getScore());
        values.put(LEVEL, score.getGameLevel());
        if (score.getLongitude() != null) {
            values.put(LATITUDE, score.getLatitude());
            values.put(LONGITUDE, score.getLongitude());
            values.put(ADDRESS, score.getAddress());
            values.put(COUNTRY, score.getCountry());
            values.put(CITY, score.getCity());
        } else {
            values.putNull(LATITUDE);
            values.putNull(LONGITUDE);
            values.putNull(ADDRESS);
            values.putNull(COUNTRY);
            values.putNull(CITY);
        }

        Long id = db.insert(RECORDS_TABLE, null, values);

        return id;

    }

    /**
     * Get the top 10 record according to game level
     */
    public ArrayList<Score> getScoreTable(gameLevel level) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "";
        ArrayList<Score> scoreTable = new ArrayList<Score>();
        switch (level) {
            default:
                return null;
            case BEGINNER: {
                selectQuery = "SELECT  * FROM " + RECORDS_TABLE + " WHERE level= 'beginner' " + " ORDER BY " + SCORE + " ASC LIMIT 10";
                break;
            }
            case MEDIUM: {
                selectQuery = "SELECT  * FROM " + RECORDS_TABLE + " WHERE level = 'medium'" + " ORDER BY " + SCORE + " ASC LIMIT 10";
                break;
            }
            case HARD: {
                selectQuery = "SELECT  * FROM " + RECORDS_TABLE + " WHERE level = 'hard'" + " ORDER BY " + SCORE + " ASC LIMIT 10";
                break;
            }
        }

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Score score = new Score();
                score.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                score.setName((c.getString(c.getColumnIndex(NAME))));
                score.setScore((c.getLong(c.getColumnIndex(SCORE))));
                score.setLatitude((c.getString(c.getColumnIndex(LATITUDE))));
                score.setLongitude((c.getString(c.getColumnIndex(LONGITUDE))));
                score.setAddress((c.getString(c.getColumnIndex(ADDRESS))));
                score.setCountry((c.getString(c.getColumnIndex(COUNTRY))));
                score.setCity((c.getString(c.getColumnIndex(CITY))));

                // adding to tags list
                scoreTable.add(score);
            } while (c.moveToNext());
        }
        return scoreTable;
    }
}
