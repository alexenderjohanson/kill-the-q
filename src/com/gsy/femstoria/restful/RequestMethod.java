package com.gsy.femstoria.restful;

public enum RequestMethod {
    GET(1),
    POST(2),
    PUT(3),
    DELETE(4);

    private int mValue;

    RequestMethod(int i) {
        this.mValue = i;
    }

    public int getValue() {
        return mValue;
    }

    static RequestMethod fromValue(int value) {
        for (RequestMethod current : RequestMethod.values()) {
            if (current.getValue() == value) {
                return current;
            }
        }

        return GET;
    }
}
