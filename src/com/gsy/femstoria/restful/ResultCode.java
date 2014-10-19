package com.gsy.femstoria.restful;

public enum ResultCode {
    Success(0), Error(1), Cancel(2);

    private int value;

    private ResultCode(int param) {
        value = param;
    }

    public int getValue() {
        return value;
    }
}
