package org.yyama.multicounter.dao;

import android.os.Environment;
import android.util.Log;

import org.yyama.multicounter.model.Counter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CounterDao {
    public static String createFileName(Counter counter) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return counter.getId() + "_" + counter.getTitle() + "_" + sdf.format(new Date()) + ".txt";
    }

    public static void increment(Counter counter) {
        Log.d("counter", "in dao increment");
        if (counter.isRecording()) {
            Log.d("counter", "increment out file.");
            outFile(counter, "+1");
        }
    }

    public static void decrement(Counter counter) {
        if (counter.isRecording()) {
            Log.d("counter", "decrement out file.");
            outFile(counter, "-1");
        }
    }

    private static void outFile(Counter counter, String s) {
        FileWriter fw = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            Log.d("counter", Environment.getDataDirectory().getAbsolutePath());
            String path = Environment.getDataDirectory().getAbsolutePath() + "/data/org.yyama.multicounter/" + counter.getFileName();
            fw = new FileWriter(path, true);
            fw.append(sdf.format(counter.getLastUpdateDateTime().getTime()) + "," + s + "," + counter.getNum() + "\n");
        } catch (IOException e) {
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

    public static void reset(Counter counter) {
        if (counter.isRecording()) {
            outFile(counter, "set number");
        }
    }

    public static void setNum(Counter counter) {
        if (counter.isRecording()) {
            outFile(counter, "set number");
        }
    }
}
