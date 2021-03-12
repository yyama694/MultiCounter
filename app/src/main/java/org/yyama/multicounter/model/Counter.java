package org.yyama.multicounter.model;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Counter implements Serializable {
    private String id;
    private String groupId;
    private String title;
    private long num = 1;
    private String fileName;
    private boolean recording = false;
    private CounterSize size;

    public Counter(String id, String groupId, String title, long num, String fileName, boolean recording, Calendar lastUpdateDateTime, CounterSize size) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.num = num;
        this.fileName = fileName;
        this.recording = recording;
        this.lastUpdateDateTime = lastUpdateDateTime;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }


    public Calendar getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Calendar lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    private Calendar lastUpdateDateTime;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getNum() {
        return num;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = CounterSize.getCounterSizeById(size);
    }

    public void setNum(long num) {
        this.num = num;
        lastUpdateDateTime = Calendar.getInstance();
    }

    public void increment() {
        num++;
        lastUpdateDateTime = Calendar.getInstance();
    }

    public void decrement() {
        num--;
        lastUpdateDateTime = Calendar.getInstance();
    }

    public void startRecording() {
        if (!isRecording()) {
            Log.d("counter", "start recording");
            recording = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String name = groupId + "_" + id + "_" + title + "_" + sdf.format(new Date()) + ".txt";
            fileName = name;
        }
    }

    public void stopRecording() {
        recording = false;
    }

    public void reset() {
        num = 0;
        lastUpdateDateTime = Calendar.getInstance();
    }
}
