package org.yyama.multicounter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MultiCounterDB";
    private static final int DB_VERSION = 2;

    private static final String CREATE_TBL_SYSTEM_PARAMETER =
            "CREATE TABLE TBL_SYSTEM_PARAMETER(CURRENT_GROUP_ID INTEGER,COUNTER_GROUP_ID INTEGER,COUNTER_ID INTEGER );";
    private static final String DROP_TBL_SYSTEM_PARAMETER =
            "DROP TABLE TBL_SYSTEM_PARAMETER;";
    private static final String INSERT_TBL_SYSTEM_PARAMETER =
            "INSERT INTO TBL_SYSTEM_PARAMETER VALUES(0,0,0);";

    private static final String CREATE_TBL_COUNTER_GROUP =
            "CREATE TABLE TBL_COUNTER_GROUP(ID  TEXT PRIMARY KEY,NAME TEXT,DISPLAY_ORDER INTEGER);";
    private static final String DROP_TBL_COUNTER_GROUP = "DROP TABLE TBL_COUNTER_GROUP;";

    private static final String CREATE_TBL_COUNTER =
            "CREATE TABLE TBL_COUNTER(ID  TEXT PRIMARY KEY,COUNTER_GROUP_ID INTEGER,NAME TEXT,DISPLAY_ORDER INTEGER" +
                    ",NUMBER INTEGER,LAST_UPDATE_DATETIME TEXT);";
    private static final String DROP_TBL_COUNTER = "DROP TABLE TBL_COUNTER;";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("counterDB", "dbHelper onCreate");
        db.execSQL(CREATE_TBL_SYSTEM_PARAMETER);
        db.execSQL(CREATE_TBL_COUNTER_GROUP);
        db.execSQL(CREATE_TBL_COUNTER);
        db.execSQL(INSERT_TBL_SYSTEM_PARAMETER);
        addSizeColumnToCounter(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("counterDB","dbHelper onUpgrade");
        Log.d("counterDB","-- dbHelper oldVersion " + String.valueOf(oldVersion));
        Log.d("counterDB","-- dbHelper newVersion " + String.valueOf(newVersion));

        if (oldVersion <= 1 && 2 <= newVersion) {
            addSizeColumnToCounter(db);
        } else {
            Log.d("counter", "DB onUpgrade");
            db.execSQL(DROP_TBL_SYSTEM_PARAMETER);
            db.execSQL(DROP_TBL_COUNTER_GROUP);
            db.execSQL(DROP_TBL_COUNTER);
            db.execSQL(CREATE_TBL_SYSTEM_PARAMETER);
            db.execSQL(CREATE_TBL_COUNTER_GROUP);
            db.execSQL(CREATE_TBL_COUNTER);
        }
    }

    private void addSizeColumnToCounter(SQLiteDatabase db) {
        db.execSQL("alter table TBL_COUNTER add column SIZE INTEGER NOT NULL default 1;");
    }
}
