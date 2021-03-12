package org.yyama.multicounter.model;

import android.util.Log;

import org.yyama.multicounter.dao.CounterDao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CounterGroup implements Serializable {
    public String getId() {
        return id;
    }
    private String id;
    private List<Counter> counterGroup;
    private String title;
    private boolean recording = false;
    private String fileName="";

    public CounterGroup(String id, String title, List<Counter> counterGroup) {
        this.id = id;
        this.title = title;
        this.counterGroup = counterGroup;
        this.recording = false;
        this.fileName="";
    }

    public void startRecording() {
        if (!isRecording()) {
            Log.d("counter", "start recording");
            recording = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String name = id + "_" + title + "_" + sdf.format(new Date()) + ".txt";
            fileName = name;
        }
    }

    public boolean isRecording() {
        return recording;
    }
    public void stopRecording() {
        recording = false;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<Counter> getCounterGroup() {
        return counterGroup;
    }

    public Counter findByid(String id) {
        Counter result = null;
        for (Counter c :
                counterGroup) {
            if (c.getId().equals(id)) {
                result = c;
            }
        }
        return result;
    }

    public void addCounter(String title, CounterDao dao) {
        Counter c = new Counter(dao.getNextId(), id, title, 1, "", false, Calendar.getInstance(),CounterSize.MEDIUM);
        counterGroup.add(c);
    }

    public void deleteCounter(String id) {
        for (int i = 0; i < counterGroup.size(); i++) {
            if (id.equals(counterGroup.get(i).getId())) {
                counterGroup.remove(i);
                break;
            }
        }
    }

    public Calendar getCounterLastUpdateDateTime() {
        Calendar result = Calendar.getInstance();
        result.set(0, 0, 0, 0, 0, 0);
        for (int i = 0; i < counterGroup.size(); i++) {
            Counter c = counterGroup.get(i);
            if (result.compareTo(c.getLastUpdateDateTime()) < 0) {
                result = c.getLastUpdateDateTime();
            }
        }
        return result;
    }

    public String getFileName() {
        return fileName;
    }
}
