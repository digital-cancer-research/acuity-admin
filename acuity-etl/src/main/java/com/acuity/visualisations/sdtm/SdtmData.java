package com.acuity.visualisations.sdtm;

import com.acuity.visualisations.sdtm.entity.SdtmEntity;
import com.acuity.visualisations.sdtm.entity.SdtmKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdtmData<T extends SdtmEntity> {
    private final SdtmDomain domain;
    private final Map<SdtmKey, List<T>> map = new HashMap<>();

    public SdtmData(SdtmDomain domain) {
        this.domain = domain;
    }

    public Map<SdtmKey, List<T>> getData() {
        return map;
    }

    public SdtmDomain getDomain() {
        return domain;
    }

    public void add(SdtmKey key, T val) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
    }
}
