package org.yyama.multicounter.model;

public enum CounterSize {
    SMALL(0),
    MEDIUM(1),
    LARGE(2);
    private int id;

    private CounterSize(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CounterSize getCounterSizeById(int id ) {
        switch (id) {
            case 0:
                return SMALL;
            case 1:
                return MEDIUM;
            case 2:
                return LARGE;
        }
        throw new RuntimeException("CounterSize#getCounterSizeById id が想定外です。" + String.valueOf(id));
//        return MEDIUM;
    }

}
