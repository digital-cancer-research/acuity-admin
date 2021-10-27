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
