package org.yyama.multicounter.model;

public class CounterId {
    private CounterId() {
    }
    private static long currentId = 0;

    public static void setCurrentId(long id) {
        currentId = id;
    }

    public static String nextId() {
        return "counter_" + currentId++;
    }
}
