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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("cntlinHolder")
@Scope("prototype")
public class CntlinDataHolder extends JobExecutionInfoAware {

    private Map<String, Map<String, String>> cntlinMap = new HashMap<String, Map<String, String>>();

    public Map<String, String> getFmtMap(String fmtName) {
        return cntlinMap.get(fmtName);
    }

    public void setFmtMap(String fmtName, Map<String, String> fmtMap) {
        cntlinMap.put(fmtName, fmtMap);
    }

    public String getValue(String fmtName, String fmtValue) {
        if (cntlinMap.get(fmtName) == null) {
            return null;
        }
        return cntlinMap.get(fmtName).get(fmtValue);
    }

}
