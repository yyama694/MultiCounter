package org.yyama.multicounter.dao;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.view.MainActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CounterDao {
    public CounterDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    DBHelper dbHelper;

    public String createFileName(Counter counter) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return counter.getId() + "_" + counter.getTitle() + "_" + sdf.format(new Date()) + ".txt";
    }

    public void outFile(Counter counter, String text, MainActivity mainActivity) {
            outFile1(counter, text, mainActivity);

    }

    private void outFile1(Counter counter, String text, MainActivity mainActivity) {
        FileWriter fw = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            String path = mainActivity.getExternalFilesDir(Environment.getDataDirectory().toString()) + "/" + counter.getFileName();
            Log.d("counter", "path:" + path);
            fw = new FileWriter(path, true);
            fw.append(sdf.format(counter.getLastUpdateDateTime().getTime()) + "," + text + "," + counter.getNum() + "\n");
            Log.d("counter", "ファイル書き込み完了");
        } catch (IOException e) {
            Log.d("counter", "outFileで例外！！");
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static final String SELECT_ID = "SELECT COUNTER_ID FROM TBL_SYSTEM_PARAMETER";
    private static final String SELECT_ID_COUNT = "SELECT COUNT(*) FROM TBL_COUNTER WHERE ID = ?";
    private static final String UPDATE_COUNTER_ID = "UPDATE TBL_SYSTEM_PARAMETER SET COUNTER_ID = ?";

    public String getNextId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c1 = db.rawQuery(SELECT_ID, null);
        c1.moveToFirst();
        int next = c1.getInt(0);
        int count;
        String id;
        do {
            next++;
            id = "counter_" + String.valueOf(next);
            Cursor c2 = db.rawQuery(SELECT_ID_COUNT, new String[]{id});
            c2.moveToFirst();
            count = c2.getInt(0);
        } while (count != 0);
        db.execSQL(UPDATE_COUNTER_ID, new String[]{String.valueOf(next)});
        return id;
    }

    private static final String SELECT_GROUP_ID = "SELECT COUNTER_GROUP_ID FROM TBL_SYSTEM_PARAMETER";
    private static final String SELECT_GROUP_ID_COUNT = "SELECT COUNT(*) FROM TBL_COUNTER_GROUP WHERE ID = ?";
    private static final String UPDATE_COUNTER_GROUP_ID = "UPDATE TBL_SYSTEM_PARAMETER SET COUNTER_GROUP_ID = ?";

    public String getNextGroupId() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c1 = db.rawQuery(SELECT_GROUP_ID, null);
        c1.moveToFirst();
        int next = c1.getInt(0);
        int count;
        String id;
        do {
            next++;
            id = "counter_" + String.valueOf(next);
            Cursor c2 = db.rawQuery(SELECT_GROUP_ID_COUNT, new String[]{id});
            c2.moveToFirst();
            count = c2.getInt(0);
        } while (count != 0);
        db.execSQL(UPDATE_COUNTER_GROUP_ID, new String[]{String.valueOf(next)});
        Log.d("counter", "counter id:" + id);
        return id;
    }


}
