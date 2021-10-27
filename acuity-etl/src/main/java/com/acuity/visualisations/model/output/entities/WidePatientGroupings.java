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

package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.output.FieldDelegatingEntity;
import com.acuity.visualisations.model.output.SplitEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WidePatientGroupings extends TimestampedEntity implements FieldDelegatingEntity, SplitEntity<PatientGroup> {

    private String studyGuid;
    private String subject;
    private String part;
    private Map<String, Object> groupings = new LinkedHashMap<>();

    @Override
    public List<PatientGroup> split() {
        return groupings.entrySet().stream().map(entry -> {
            PatientGroup group = new PatientGroup();
            group.setStudyGuid(studyGuid);
            group.setSubject(subject);
            group.setPart(part);
            group.setGroupingName(entry.getKey());
            if (entry.getValue() != null) {
                group.setGroupName(entry.getValue().toString());
            }
            return group;
        }).collect(Collectors.toList());
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @Override
    public Set<String> getFieldNames() {
        return groupings.keySet();
    }

    @Override
    public void setField(String name, Object value) {
        groupings.put(name, value);
    }

    @Override
    public Object getField(String name) {
        return groupings.get(name);
    }
}
