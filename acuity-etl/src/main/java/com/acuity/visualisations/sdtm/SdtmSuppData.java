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
