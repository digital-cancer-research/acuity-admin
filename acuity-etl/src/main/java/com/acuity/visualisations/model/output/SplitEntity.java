package com.acuity.visualisations.model.output;

import java.util.List;

public interface SplitEntity<T extends OutputEntity> {
    /**
     * Split current entity to list of entities
     * For example, split ECG to list of EGs
     *
     * @return
     */
    List<T> split();
}
