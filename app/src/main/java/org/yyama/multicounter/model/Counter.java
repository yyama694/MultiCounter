package org.yyama.multicounter.model;

import android.util.Log;


import java.io.Serializable;
import java.util.Calendar;

public class Counter implements Serializable {
    private String id;
    private String title;
    private long num = 1;
    private String fileName;
    private boolean recoding=false;

    public Counter(String id, String title, long num, String fileName, boolean recording, Calendar lastUpdateDateTime) {
        this.id = id;
        this.title = title;
        this.num = num;
        this.fileName = fileName;
        this.recording = recording;
        this.lastUpdateDateTime = lastUpdateDateTime;
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

    private boolean recording = false;

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
        if(!isRecording()) {
            Log.d("counter","start recording");
            recording = true;
        }
    }

    public void stopRecording() {
        recording=false;
    }

    public void reset() {
        num = 0;
        lastUpdateDateTime = Calendar.getInstance();
    }
}
