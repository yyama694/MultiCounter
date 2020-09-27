package org.yyama.multicounter.model;

import org.yyama.multicounter.dao.CounterDao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class CounterGroup implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private List<Counter> counterGroup;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public CounterGroup(String id, String title, List<Counter> counterGroup) {
        this.id = id;
        this.title = title;
        this.counterGroup = counterGroup;
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
        Counter c = new Counter(dao.getNextId(), title, 1, "", false, Calendar.getInstance());
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
}
