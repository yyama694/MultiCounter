package org.yyama.multicounter.dao;

import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;
import org.yyama.multicounter.model.CounterGroups;
import org.yyama.multicounter.model.CounterId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CounterGroupsDao {
    private CounterGroupsDao(){}
    public static CounterGroups loadAll() {
        Counter c = new Counter();
        c.setId(CounterId.nextId());
        c.setNum(1);
        c.setTitle("counter text");
        c.setLastUpdateDateTime(Calendar.getInstance());
        Counter c2 = new Counter();
        c2.setId(CounterId.nextId());
        c2.setNum(2);
        c2.setTitle("2個め");
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.YEAR,-1);
        c2.setLastUpdateDateTime(cal2);
        Counter c3 = new Counter();
        c3.setId(CounterId.nextId());
        c3.setNum(2);
        c3.setTitle("3個め");
        Calendar cal3 = Calendar.getInstance();
        cal2.add(Calendar.MONTH,-1);
        c2.setLastUpdateDateTime(cal2);
        c3.setLastUpdateDateTime(Calendar.getInstance());
        Counter c4 = new Counter();
        c4.setId(CounterId.nextId());
        c4.setNum(2);
        c4.setTitle("4個め");
        c4.setLastUpdateDateTime(Calendar.getInstance());
        Counter c5 = new Counter();
        c5.setId(CounterId.nextId());
        c5.setNum(9);
        c5.setTitle("5個め");
        c5.setLastUpdateDateTime(Calendar.getInstance());
        List<Counter> counters=new ArrayList<>();
        counters.add(c);
        counters.add(c2);
        counters.add(c3);
        counters.add(c4);
        counters.add(c5);
        CounterGroup cg = new CounterGroup(counters);
        cg.setTitle("counter group A");
        cg.setId("counter group A");
        CounterGroups counterGroups = new CounterGroups();
        counterGroups.setCurrentGroupId("counter group A");
        List<CounterGroup> cgList = new ArrayList<>();
        cgList.add(cg);
        counterGroups.setCounterGroupList(cgList);

        return counterGroups;
    }
}
