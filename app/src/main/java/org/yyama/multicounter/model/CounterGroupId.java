package org.yyama.multicounter.model;

public class CounterGroupId {
    private CounterGroupId() {
    }
    private static long currentId = 1;

    public static void setCurrentId(long id) {
        currentId = id;
    }

    public static String nextId() {
        return "counterGroup_" + ++currentId;
    }
}
