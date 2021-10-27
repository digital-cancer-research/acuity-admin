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

package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.sdtm.SdtmData;
import com.acuity.visualisations.sdtm.SdtmSuppData;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores Supplemental data per file
 */
@Component
@Scope("prototype")
public class SdtmDataHolder {
    private Map<String, SdtmData> mainMap = new HashMap<>();
    private Map<String, SdtmSuppData> suppMap = new HashMap<>();


    public SdtmData getMainData(String file) {
        return mainMap.get(file);
    }

    public boolean containsMainData(String file) {
        SdtmData data = mainMap.get(file);
        return data != null && data.getData().size() > 0;
    }

    public boolean containsSuppData(String file) {
        SdtmSuppData data = suppMap.get(file);
        return data != null && data.getData().size() > 0;
    }

    public void putMainData(String file, SdtmData data) {
        mainMap.put(file, data);
    }

    public SdtmSuppData getSuppData(String file) {
        return suppMap.get(file);
    }

    public void putSuppData(String file, SdtmSuppData data) {
        suppMap.put(file, data);
    }

}
