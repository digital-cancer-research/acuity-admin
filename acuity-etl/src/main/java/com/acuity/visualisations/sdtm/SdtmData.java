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
