package org.yyama.multicounter.model;

import java.io.Serializable;
import java.util.List;

public class CounterGroups implements Serializable {
    private List<CounterGroup> counterGroupList;

    public List<CounterGroup> getCounterGroupList() {
        return counterGroupList;
    }

    private String currentGroupId;

    public void setCounterGroupList(List<CounterGroup> counterGroupList) {
        this.counterGroupList = counterGroupList;
    }

    public String getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(String currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public CounterGroup getCurrentCounterGroup() {
        for (int i = 0; i < counterGroupList.size(); i++) {
            if (counterGroupList.get(i).getId().equals(currentGroupId)) {
                return counterGroupList.get(i);
            }
        }
        return counterGroupList.get(0);
    }

    public void addCounterGroup(CounterGroup cg) {
        counterGroupList.add(cg);
    }

    public void deleteCounterGroup(String id) {
        for (int i = 0; i < counterGroupList.size(); i++) {
            if (id.equals(counterGroupList.get(i).getId())) {
                counterGroupList.remove(i);
                break;
            }
        }
    }

    public CounterGroup findCounterGroupsById(String id) {
        for (CounterGroup cg : counterGroupList) {
            if (cg.getId().equals(id)) {
                return cg;
            }
        }
        return null;
    }
}
