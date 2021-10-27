package com.acuity.visualisations.sdtm;

import com.acuity.visualisations.sdtm.entity.SdtmKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdtmSuppData {
    //Map<(USUBJID), Map<SEQ, Map<QNAM, List<QVAL>>>>
    private Map<SdtmKey, Map<String, Map<String, List<String>>>> data = new HashMap<>();

    public List<String> get(SdtmKey key, String seq, String qNam) {
        if (data.containsKey(key) && data.get(key).containsKey(seq)) {
            return data.get(key).get(seq).get(qNam);
        }
        return null;
    }

    public Map<SdtmKey, Map<String, Map<String, List<String>>>> getData() {
        return data;
    }

    public void add(SdtmKey key, String seq, String qNam, String qVal) {
        data.computeIfAbsent(key, k -> new HashMap<>()).
                computeIfAbsent(seq, s -> new HashMap<>()).
                computeIfAbsent(qNam, q -> new ArrayList<>(1)).add(qVal);
    }
}
