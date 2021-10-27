package com.acuity.visualisations.web.dto;

import java.util.Map;
import java.util.TreeMap;

public class ModulesStatusDTO {

    private boolean blinded;
    private Map<Long, Boolean> mapped = new TreeMap<Long, Boolean>();

    public boolean isBlinded() {
        return blinded;
    }

    public void setBlinded(boolean blinded) {
        this.blinded = blinded;
    }

    public Map<Long, Boolean> getMapped() {
        return mapped;
    }
}
