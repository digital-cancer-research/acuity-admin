/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
