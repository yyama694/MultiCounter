package org.yyama.multicounter.model;

import android.util.Log;

import org.yyama.multicounter.dao.CounterDao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Counter implements Serializable {
    private String id;
    private String title;
    private long num = 1;
    private long order;
    private String fileName;
    private boolean recoding=false;

    public Counter(String id, String title, long num, long order, String fileName, boolean recoding, boolean recording, Calendar lastUpdateDateTime) {
        this.id = id;
        this.title = title;
        this.num = num;
        this.order = order;
        this.fileName = fileName;
        this.recoding = recoding;
        this.recording = recording;
        this.lastUpdateDateTime = lastUpdateDateTime;
    }
    public Counter() {}

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
        CounterDao.setNum(this);
    }

    public void increment() {
        num++;
        lastUpdateDateTime = Calendar.getInstance();
        CounterDao.increment(this);
    }

    public void decrement() {
        num--;
        lastUpdateDateTime = Calendar.getInstance();
        CounterDao.decrement(this);
    }

    public void startRecording() {
        if(!isRecording()) {
            Log.d("counter","start recording");
            recording = true;
            fileName = CounterDao.createFileName(this);
        }
    }

    public void stopRecording() {
        recording=false;
    }

    public void reset() {
        num = 0;
        lastUpdateDateTime = Calendar.getInstance();
        CounterDao.reset(this);
    }
}
