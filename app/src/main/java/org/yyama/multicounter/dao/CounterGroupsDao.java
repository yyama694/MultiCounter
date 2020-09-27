package org.yyama.multicounter.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;
import org.yyama.multicounter.model.CounterGroups;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CounterGroupsDao {
    final String SELECT_GROUP = "SELECT ID, NAME FROM TBL_COUNTER_GROUP ORDER BY DISPLAY_ORDER;";
    final String SELECT_COUNTERS_BY_GROUP_ID = "SELECT ID, COUNTER_GROUP_ID, NAME, NUMBER, LAST_UPDATE_DATETIME FROM TBL_COUNTER WHERE COUNTER_GROUP_ID = ? ORDER BY DISPLAY_ORDER;";

    private DBHelper dbHelper;

    public CounterGroupsDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public CounterGroups loadAll(CounterDao dao) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor groupCursor = db.rawQuery(SELECT_GROUP, null);
        Log.d("counter", "カウンターグループ行数：" + groupCursor.getCount());

        // カウンターグループ、カウンター作成
        List<CounterGroup> cgList = new ArrayList<>();
        while (groupCursor.moveToNext()) {
            Log.d("counter", "カウンターグループ作成：" + groupCursor.getString(1));

            List<Counter> counters = new ArrayList<>();
            Cursor counterCursor = db.rawQuery(SELECT_COUNTERS_BY_GROUP_ID, new String[]{groupCursor.getString(0)});
            while (counterCursor.moveToNext()) {
                Log.d("counter", "カウンター作成：" + counterCursor.getString(2));

                SimpleDateFormat sdf = new SimpleDateFormat();
                try {
                    Calendar cal = Calendar.getInstance();
                    Log.d("counter", "getString(4):" + counterCursor.getString(4));
                    Date d = sdf.parse(counterCursor.getString(4));
                    cal.setTime(d);
                    Counter counter = new Counter(counterCursor.getString(0),
                            counterCursor.getString(2),
                            counterCursor.getLong(3),
                            "",
                            false,
                            cal);
                    counters.add(counter);
                } catch (ParseException e) {
                    Log.d("counter", "日付へのパース失敗。" + counterCursor.getString(4));
                } finally {
                }
            }
            counterCursor.close();
            CounterGroup counterGroup = new CounterGroup(groupCursor.getString(0), groupCursor.getString(1), counters);
            cgList.add(counterGroup);
        }
        groupCursor.close();
        db.close();

        if (cgList.size() == 0) {
            Log.d("counter", "DBにデータ登録がないのでデフォルトグループ、デフォルトカウンターを作ります。");
            Counter c = new Counter(dao.getNextId(), "カウンター１", 1, "", false, Calendar.getInstance());
            List<Counter> counters = new ArrayList<>();
            counters.add(c);
            CounterGroup cg = new CounterGroup(dao.getNextGroupId(), "counter group A", counters);
            cgList = new ArrayList<>();
            cgList.add(cg);
        }
        CounterGroups counterGroups = new CounterGroups(cgList);
        saveAll(counterGroups);

        return counterGroups;
    }


    public void saveAll(CounterGroups counterGroups) {
        Log.d("counter", "counterGroupsDao#saveAll");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("BEGIN;");

            // 全削除
            db.execSQL("DELETE FROM TBL_COUNTER;");
            db.execSQL("DELETE FROM TBL_COUNTER_GROUP;");

            // 全追加
            insertAll(db, counterGroups);

            db.execSQL("COMMIT;");

        } catch (Exception e) {
            db.execSQL("ROLLBACK;");
            throw e;
        } finally {
            db.close();
        }
    }

    private static final String INSERT_COUNTER_GROUP = "INSERT INTO TBL_COUNTER_GROUP VALUES(?,?,?);";
    private static final String INSERT_COUNTER = "INSERT INTO TBL_COUNTER VALUES(?,?,?,?,?,?);";

    private void insertAll(SQLiteDatabase db, CounterGroups counterGroups) {
        for (int i = 0; i < counterGroups.getCounterGroupList().size(); i++) {
            CounterGroup cg = counterGroups.getCounterGroupList().get(i);
            db.execSQL(INSERT_COUNTER_GROUP, new String[]{cg.getId(), cg.getTitle(), String.valueOf(i)});
            for (int j = 0; j < cg.getCounterGroup().size(); j++) {
                Counter c = cg.getCounterGroup().get(j);
                db.execSQL(INSERT_COUNTER, new String[]{c.getId(), cg.getId(), c.getTitle(), String.valueOf(j), String.valueOf(c.getNum()), new SimpleDateFormat().format(c.getLastUpdateDateTime().getTime())});
                Log.d("counter", "counter 追加完了：" + c.getId() + "," + c.getTitle());
            }
            Log.d("counter", "counter group 追加完了：" + cg.getId() + "," + cg.getTitle());
        }
    }

    private static final String UPDATE_CURRENT_GROUP = "UPDATE TBL_SYSTEM_PARAMETER SET CURRENT_GROUP_ID = ?;";

    public void updateCurrentGroup(String id) {
        dbHelper.getWritableDatabase().execSQL(UPDATE_CURRENT_GROUP, new String[]{id});
        dbHelper.close();
    }

    private static final String SELECT_CURRENT_GROUP = "SELECT CURRENT_GROUP_ID FROM TBL_SYSTEM_PARAMETER;";

    public String selectCurrentGroup() {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_CURRENT_GROUP, new String[]{});
        c.moveToNext();
        return c.getString(0);
    }

}
