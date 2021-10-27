package com.acuity.visualisations.dal.util;

public enum State {

    JUST_INSERTED(0), JUST_UPDATED(1), OLD(2), SYNCHRONIZED(3), TMP_JUST_INSERTED(4), TMP_JUST_UPDATED(5), TMP_SYNCHRONIZED(6);

    private final int intValue;

    State(int intValue) {
        this.intValue = intValue;
    }

    public int getValue() {
        return intValue;
    }

    public String getStringValue() {
        return Integer.toString(intValue);
    }

    public static State getState(String value) {
        if (value == null) {
            return null;
        }
        Integer val = Integer.parseInt(value);
        for (State state : State.values()) {
            if (state.getValue() == val) {
                return state;
            }
        }
        return null;
    }

}
