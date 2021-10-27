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

package com.acuity.visualisations.batch.holders;

import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class RowParameters implements Serializable {

    private static final long serialVersionUID = 3565471956761185683L;
    private int secondaryHash;
    private String id;
    private State state;
    private boolean actionPerformed;

    public RowParameters(int secondaryHash, String id, State state, boolean actionPerformed) {
        this.secondaryHash = secondaryHash;
        this.id = id;
        this.state = state;
        this.actionPerformed = actionPerformed;
    }

    public static RowParameters createFromPreviousEtlItem(RowParameters prototype) {
        RowParameters rowParameters = new RowParameters(prototype.secondaryHash, prototype.id, State.SYNCHRONIZED, false);
        return rowParameters;
    }

    public int getSecondaryHash() {
        return secondaryHash;
    }

    public String getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public boolean isActionPerformed() {
        return actionPerformed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("id", id).append("secondaryHash", secondaryHash)
                .append("state", state).toString();
    }

}
